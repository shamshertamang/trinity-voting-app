// src/components/CandidateForm.jsx
import { useEffect, useState } from 'react';
import { getCandidates, submitVote } from '../lib/api';

function validateEmail(email) {
    // must end with @trincoll.edu and contain exactly one dot (the one in the domain)
    const emailRegex = /^[^.]+@trincoll\.edu$/;
    const dotCount = (email.match(/\./g) || []).length;
    return emailRegex.test(email) && dotCount === 1;
}

export default function CandidateForm() {
    const [email, setEmail] = useState('');
    const [emailError, setEmailError] = useState('');

    const [mode, setMode] = useState('select'); // 'select' | 'type'
    const [candidates, setCandidates] = useState([]);
    const [loading, setLoading] = useState(true);
    const [candError, setCandError] = useState('');

    const [selectedCandidate, setSelectedCandidate] = useState('');
    const [newCandidate, setNewCandidate] = useState('');

    const [message, setMessage] = useState({ text: '', type: '' }); // 'success' | 'error'
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        let ignore = false;
        (async () => {
            try {
                setLoading(true);
                setCandError('');
                const list = await getCandidates();
                if (!ignore) {
                    setCandidates(list || []);
                    setSelectedCandidate('');
                }
            } catch {
                if (!ignore) setCandError('Error loading candidates');
            } finally {
                if (!ignore) setLoading(false);
            }
        })();
        return () => { ignore = true; };
    }, []);

    async function reloadCandidates() {
        try {
            setLoading(true);
            setCandError('');
            const list = await getCandidates();
            setCandidates(list || []);
            setSelectedCandidate('');
        } catch {
            setCandError('Error loading candidates');
        } finally {
            setLoading(false);
        }
    }

    async function onSubmit(e) {
        e.preventDefault();
        setMessage({ text: '', type: '' });

        if (!validateEmail(email)) {
            setEmailError('Please enter a valid @trincoll.edu email address with no additional dots.');
            return;
        }
        setEmailError('');

        let candidateName = mode === 'select'
            ? selectedCandidate.trim()
            : newCandidate.trim();

        if (!candidateName) {
            setMessage({
                text: mode === 'select' ? 'Please select a candidate.' : 'Please enter a candidate name.',
                type: 'error'
            });
            return;
        }

        try {
            setSubmitting(true);
            const data = await submitVote({ voterEmail: email.trim(), candidateName });
            if (data?.success) {
                setMessage({ text: 'Vote submitted successfully!', type: 'success' });
                setEmail('');
                setMode('select');
                setSelectedCandidate('');
                setNewCandidate('');
                await reloadCandidates();
            } else {
                setMessage({ text: data?.message || 'Error submitting vote.', type: 'error' });
            }
        } catch (err) {
            setMessage({ text: err.message || 'Error submitting vote. Please try again.', type: 'error' });
        } finally {
            setSubmitting(false);
        }
    }

    return (
        <div className="container">
            <h1>Trinity College Voting System</h1>

            <form onSubmit={onSubmit} noValidate>
                <div className="form-group">
                    <label htmlFor="email">Email Address:</label>
                    <input
                        id="email" type="email" placeholder="yourname@trincoll.edu"
                        value={email} onChange={(e) => setEmail(e.target.value)} required
                    />
                    {emailError && <div id="emailError" className="error">{emailError}</div>}
                </div>

                <div className="form-group">
                    <label>Choose your voting option:</label>

                    <div className="radio-row">
                        <input
                            id="selectCandidate" type="radio" name="voteOption" value="select"
                            checked={mode === 'select'} onChange={() => setMode('select')}
                        />
                        <label htmlFor="selectCandidate">Select from existing candidates</label>
                    </div>

                    <div className="radio-row">
                        <input
                            id="typeCandidate" type="radio" name="voteOption" value="type"
                            checked={mode === 'type'} onChange={() => setMode('type')}
                        />
                        <label htmlFor="typeCandidate">Type a new candidate name</label>
                    </div>
                </div>

                {mode === 'select' && (
                    <div className="form-group">
                        <label htmlFor="candidateList">Select Candidate:</label>
                        <select
                            id="candidateList" value={selectedCandidate}
                            onChange={(e) => setSelectedCandidate(e.target.value)}
                            disabled={loading || !!candError}
                        >
                            {loading && <option>Loading candidates...</option>}
                            {!loading && candError && <option>Error loading candidates</option>}
                            {!loading && !candError && (
                                <>
                                    <option value="">-- Select a candidate --</option>
                                    {candidates.map(c => (
                                        <option key={c.name} value={c.name}>{c.name}</option>
                                    ))}
                                </>
                            )}
                        </select>
                    </div>
                )}

                {mode === 'type' && (
                    <div className="form-group">
                        <label htmlFor="newCandidate">Enter Candidate Name:</label>
                        <input
                            id="newCandidate" type="text" placeholder="Enter candidate name"
                            value={newCandidate} onChange={(e) => setNewCandidate(e.target.value)}
                        />
                    </div>
                )}

                <button type="submit" disabled={submitting}>
                    {submitting ? 'Submitting…' : 'Submit Vote'}
                </button>

                {message.text && (
                    <div className={message.type === 'success' ? 'success' : 'error'} style={{ marginTop: 8 }}>
                        {message.text}
                    </div>
                )}
            </form>
        </div>
    );
}

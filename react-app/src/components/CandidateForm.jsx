import { useEffect, useState } from 'react';
import { getCandidates, submitVote } from '../lib/api';
import { getVoteByEmail, updateVote, deleteVote } from '../lib/api';
import VoteResults from './VoteResults';

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

    const [refreshKey, setRefreshKey] = useState(0);

    // already-voted state
    const [existingVote, setExistingVote] = useState(null); // { voterEmail, candidateName, ... } or null
    const [checkingVote, setCheckingVote] = useState(false);

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

    // --- Already voted helpers ---
    async function checkExistingVote(emailVal) {
        if (!validateEmail(emailVal)) {
            setExistingVote(null);
            return;
        }
        try {
            setCheckingVote(true);
            const v = await getVoteByEmail(emailVal.trim());
            setExistingVote(v); // null if none
        } catch {
            setExistingVote(null);
        } finally {
            setCheckingVote(false);
        }
    }

    function startEditVote() {
        if (!existingVote) return;
        setMode('select');
        // Try to prefill from existing vote
        const prev = existingVote.candidateName || existingVote.candidate || '';
        setSelectedCandidate(prev);
    }

    async function handleDeleteVote() {
        if (!validateEmail(email)) return;
        try {
            const resp = await deleteVote(email.trim());
            if (resp?.success) {
                setMessage({ text: 'Your vote was deleted.', type: 'success' });
                setExistingVote(null);
                await reloadCandidates();
                setRefreshKey(k => k + 1);
            } else {
                setMessage({ text: resp?.message || 'Could not delete vote.', type: 'error' });
            }
        } catch (e) {
            setMessage({ text: e.message || 'Could not delete vote.', type: 'error' });
        }
    }

    // Re-check when email becomes valid (and also when user leaves the field)
    useEffect(() => {
        if (validateEmail(email)) checkExistingVote(email);
        else setExistingVote(null);
    }, [email]);

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

            // If a vote exists for this email, treat this as an update.
            const action = existingVote ? updateVote : submitVote;
            const data = await action({ voterEmail: email.trim(), candidateName });

            if (data?.success) {
                setMessage({ text: existingVote ? 'Vote updated successfully!' : 'Vote submitted successfully!', type: 'success' });
                // keep email so they can update again if desired; only clear fields
                setMode('select');
                setSelectedCandidate('');
                setNewCandidate('');
                await reloadCandidates();
                setRefreshKey(k => k + 1);
                // refresh existing vote card
                await checkExistingVote(email);
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
            <p>Welcome to the Trinity Voting System. To vote, edit or delete your vote, enter your email (username@trincoll.edu).</p>
            <form onSubmit={onSubmit} noValidate>
                <div className="form-group">
                    <label htmlFor="email">Email Address:</label>
                    <input
                        id="email" type="email" placeholder="yourname@trincoll.edu"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        onBlur={() => checkExistingVote(email)}
                        required
                    />
                    {emailError && <div id="emailError" className="error">{emailError}</div>}
                </div>

                {/* Already voted? card — appears right under Email */}
                {(checkingVote || existingVote !== null) && (
                    <div className="already-voted">
                        {checkingVote && <div className="muted">Checking your vote…</div>}
                        {!checkingVote && existingVote && (
                            <div className="av-card">
                                <div>
                                    <strong>You’ve already voted.</strong>
                                    <div className="av-line">
                                        Current choice:{' '}
                                        <span className="av-pill">
                      {existingVote.candidateName || existingVote.candidate}
                    </span>
                                    </div>
                                </div>
                                <div className="av-actions">
                                    <button type="button" className="btn-secondary" onClick={startEditVote}>
                                        Edit
                                    </button>
                                    <button type="button" className="btn-danger" onClick={handleDeleteVote}>
                                        Delete
                                    </button>
                                </div>
                            </div>
                        )}
                        {!checkingVote && existingVote === null && validateEmail(email) && (
                            <div className="muted">No vote on record for this email.</div>
                        )}
                    </div>
                )}

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
                                    {candidates
                                        .filter(c => (c.voteCount || 0) > 0)
                                        .map(c => (
                                            <option key={c.name} value={c.name}>
                                                {c.name}
                                            </option>
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
                    {submitting ? 'Submitting…' : (existingVote ? 'Update Vote' : 'Submit Vote')}
                </button>

                {message.text && (
                    <div className={message.type === 'success' ? 'success' : 'error'} style={{ marginTop: 8 }}>
                        {message.text}
                    </div>
                )}
            </form>

            <VoteResults refreshKey={refreshKey} />
        </div>
    );
}

// src/components/VoteResults.jsx

import { useEffect, useState } from 'react';
import { getResults } from '../lib/api';

export default function VoteResults({ refreshKey = 0, autoRefreshMs = 0 }) {
    const [rows, setRows] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState('');

    async function load() {
        try {
            setErr('');
            setLoading(true);
            const list = await getResults();
            setRows(list);
        } catch (e) {
            setErr(e.message || 'Error loading results');
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => { load(); }, [refreshKey]);

    useEffect(() => {
        if (!autoRefreshMs) return;
        const id = setInterval(load, autoRefreshMs);
        return () => clearInterval(id);
    }, [autoRefreshMs]);

    // Hide the whole block if there are no votes at all
    const totalVotes = rows.reduce((s, r) => s + (r.votes || 0), 0);
    if (!loading && !err && totalVotes === 0) return null;

    // Sort by votes desc then name
    const sorted = [...rows]
        .filter(r => (r.votes || 0) > 0)
        .sort((a, b) => b.votes - a.votes || a.name.localeCompare(b.name));

    const maxVotes = Math.max(0, ...sorted.map(r => r.votes || 0));
    const winners = new Set(sorted.filter(r => r.votes === maxVotes && maxVotes > 0).map(r => r.name));

    return (
        <section className="results">
            <div className="results-header">
                <h2>Current Votes</h2>
                <button className="results-refresh" onClick={load} aria-label="Refresh results">↻ Refresh</button>
            </div>

            {loading && <div className="muted">Loading…</div>}
            {err && <div className="error" role="alert">{err}</div>}

            {!loading && !err && (
                <ul className="results-list" role="list">
                    {sorted.map(r => (
                        <li
                            key={r.name}
                            className={`result-row ${winners.has(r.name) ? 'winner' : ''}`}
                            aria-current={winners.has(r.name) ? 'true' : undefined}
                        >
              <span className="result-name">
                {r.name} {winners.has(r.name) && <span className="winner-badge">Leader</span>}
              </span>
                            <span className="result-votes">{r.votes}</span>
                        </li>
                    ))}
                </ul>
            )}
        </section>
    );
}

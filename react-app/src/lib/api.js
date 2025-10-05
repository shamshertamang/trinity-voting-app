// src/lib/api.js

const API_BASE =
    import.meta.env.VITE_API_BASE_URL?.replace(/\/+$/, '') || ''; // '' means same-origin (with proxy)

async function jsonOrThrow(res, defaultMessage) {
    if (res.ok) return res.json();
    let msg = defaultMessage;
    try {
        const data = await res.json();
        if (data?.message) msg = data.message;
    } catch {}
    throw new Error(msg);
}

export async function getCandidates() {
    const res = await fetch(`${API_BASE}/api/candidates`);
    return jsonOrThrow(res, 'Failed to load candidates');
}

export async function submitVote({ voterEmail, candidateName }) {
    const res = await fetch(`${API_BASE}/api/vote`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ voterEmail, candidateName }),
    });
    return jsonOrThrow(res, 'Error submitting vote');
}

export async function getResults() {
    const r = await fetch(`${API_BASE}/api/results`);
    if (!r.ok) throw new Error('Failed to load results');
    const data = await r.json();

    const arr = Array.isArray(data) ? data : (data.candidates || []);
    return arr.map(c => ({
        name: c.name,
        votes: Number(c.voteCount ?? c.votes ?? c.vote_count ?? 0),
    }));
}

export async function getVoteByEmail(email) {
    const res = await fetch(`${API_BASE}/api/votes/${encodeURIComponent(email)}`);
    if (res.status === 404) return null;
    return jsonOrThrow(res, 'Failed to load vote');
}

export async function updateVote({ voterEmail, candidateName }) {
    const res = await fetch(`${API_BASE}/api/vote`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ voterEmail, candidateName }),
    });
    return jsonOrThrow(res, 'Error updating vote');
}

export async function deleteVote(email) {
    const res = await fetch(`${API_BASE}/api/vote/${encodeURIComponent(email)}`, {
        method: 'DELETE',
    });
    return jsonOrThrow(res, 'Error deleting vote');
}

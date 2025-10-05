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

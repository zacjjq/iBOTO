package iboto.service;

import iboto.data.DataStore;
import iboto.model.Admin;
import iboto.model.Ballot;
import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** All election rules live here, so the screens stay simple and the rules are easy to test. */
public class ElectionService {

    /** A candidate with the number of votes counted for them. */
    public static class Result {
        public final Candidate candidate;
        public final int votes;

        Result(Candidate candidate, int votes) {
            this.candidate = candidate;
            this.votes = votes;
        }
    }

    private final DataStore store;
    private final Admin admin = new Admin("ADMIN-1", "Election Officer", "admin", "admin123");
    private List<Voter> voters;
    private List<Candidate> candidates;
    private List<Ballot> ballots;

    public ElectionService(DataStore store) {
        this.store = store;
        load();
    }

    private void load() {
        voters = store.loadVoters();
        candidates = store.loadCandidates();
        ballots = store.loadBallots();
    }

    // ---- voter side ----

    public Voter findVoter(String id) {
        if (id == null) return null;
        String key = id.trim();
        for (Voter v : voters) {
            if (v.getId().equalsIgnoreCase(key)) return v;
        }
        return null;
    }

    public VerifyResult verify(String id) {
        Voter v = findVoter(id);
        if (v == null) return VerifyResult.NOT_REGISTERED;
        return v.hasVoted() ? VerifyResult.ALREADY_VOTED : VerifyResult.ELIGIBLE;
    }

    public List<Candidate> candidatesFor(Position position) {
        List<Candidate> list = new ArrayList<>();
        for (Candidate c : candidates) {
            if (c.getPosition() == position) list.add(c);
        }
        return list;
    }

    private Candidate findCandidate(String id) {
        for (Candidate c : candidates) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    /** Validates, saves the anonymous ballot, then marks the voter as voted. */
    public void castBallot(Voter voter, Ballot ballot) {
        if (voter.hasVoted()) throw new IllegalStateException("This voter has already voted.");
        if (!ballot.isComplete()) throw new IllegalArgumentException("Please choose a candidate for every position.");
        for (Position p : Position.values()) {
            Candidate c = findCandidate(ballot.choiceFor(p));
            if (c == null || c.getPosition() != p) {
                throw new IllegalArgumentException("Invalid selection for " + p.getLabel() + ".");
            }
        }
        ballots.add(ballot);
        voter.markVoted();
        store.saveBallots(ballots);
        store.saveVoters(voters);
    }

    // ---- results ----

    /** Candidates of one position, ranked by votes counted from the recorded ballots. */
    public List<Result> results(Position position) {
        Map<String, Integer> counts = new HashMap<>();
        for (Ballot b : ballots) counts.merge(b.choiceFor(position), 1, Integer::sum);
        List<Result> list = new ArrayList<>();
        for (Candidate c : candidatesFor(position)) list.add(new Result(c, counts.getOrDefault(c.getId(), 0)));
        list.sort((a, b) -> b.votes - a.votes);
        return list;
    }

    public boolean isTie(List<Result> ranked) {
        return ranked.size() > 1 && ranked.get(0).votes > 0 && ranked.get(0).votes == ranked.get(1).votes;
    }

    public int ballotsCast() { return ballots.size(); }

    public int registeredVoters() { return voters.size(); }

    public List<Voter> getVoters() { return Collections.unmodifiableList(voters); }

    /** Used by the demo shortcuts on the login screen. Empty string if there is none. */
    public String sampleVoterId(boolean voted) {
        for (Voter v : voters) {
            if (v.hasVoted() == voted) return v.getId();
        }
        return "";
    }

    // ---- admin side ----

    public Admin login(String username, String password) {
        boolean ok = admin.authenticate(username == null ? "" : username.trim(), password == null ? "" : password);
        return ok ? admin : null;
    }

    public Candidate addCandidate(Position position, String name, String party) {
        String cleanName = clean(name);
        checkCandidate(null, position, cleanName);
        int max = 0;
        for (Candidate c : candidates) max = Math.max(max, Integer.parseInt(c.getId().substring(1)));
        Candidate c = new Candidate(String.format("C%02d", max + 1), position, cleanName, cleanParty(party));
        candidates.add(c);
        store.saveCandidates(candidates);
        return c;
    }

    public void updateCandidate(String id, String name, String party) {
        Candidate c = findCandidate(id);
        if (c == null) throw new IllegalArgumentException("Candidate not found.");
        String cleanName = clean(name);
        checkCandidate(c, c.getPosition(), cleanName);
        c.setName(cleanName);
        c.setParty(cleanParty(party));
        store.saveCandidates(candidates);
    }

    /** Restores the sample voters, candidates and ballots. */
    public void resetDemo() {
        store.resetToSampleData();
        load();
    }

    private void checkCandidate(Candidate editing, Position position, String name) {
        if (name.isEmpty()) throw new IllegalArgumentException("Please enter the candidate's full name.");
        for (Candidate other : candidatesFor(position)) {
            if (other != editing && other.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException(name + " is already a candidate for " + position.getLabel() + ".");
            }
        }
    }

    // commas would break the CSV files, so they are removed
    private static String clean(String s) {
        return s == null ? "" : s.replace(',', ' ').trim().replaceAll("\\s+", " ");
    }

    private static String cleanParty(String s) {
        String p = clean(s);
        return p.isEmpty() ? "Independent" : p;
    }
}

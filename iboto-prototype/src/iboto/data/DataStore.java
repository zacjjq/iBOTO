package iboto.data;

import iboto.model.Ballot;
import iboto.model.Candidate;
import iboto.model.Voter;

import java.util.List;

/** ABSTRACTION: the service only knows this interface, not how or where data is saved. */
public interface DataStore {
    List<Voter> loadVoters();
    void saveVoters(List<Voter> voters);
    List<Candidate> loadCandidates();
    void saveCandidates(List<Candidate> candidates);
    List<Ballot> loadBallots();
    void saveBallots(List<Ballot> ballots);
    void resetToSampleData();
}

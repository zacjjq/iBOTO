import iboto.data.FileDataStore;
import iboto.model.*;
import iboto.service.ElectionService;
import iboto.service.VerifyResult;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Headless checks of the election rules. Run: java -cp out SelfCheck */
public class SelfCheck {
    static int failures = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "PASS  " : "FAIL  ") + what);
        if (!ok) failures++;
    }

    static Ballot ballot(String c, String k, String s) {
        Ballot b = new Ballot();
        b.choose(Position.CAPTAIN, c);
        b.choose(Position.KAGAWAD, k);
        b.choose(Position.SK_CHAIR, s);
        return b;
    }

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("iboto-test");
        FileDataStore store = new FileDataStore(dir);
        ElectionService svc = new ElectionService(store);

        check(svc.registeredVoters() == 20 && svc.ballotsCast() == 12, "sample data: 20 voters, 12 ballots");
        check(svc.verify("BRGY-2026-99999") == VerifyResult.NOT_REGISTERED, "unknown ID -> not registered");
        check(svc.verify("   ") == VerifyResult.NOT_REGISTERED, "blank ID -> not registered");
        check(svc.verify(" brgy-2026-00101 ") == VerifyResult.ALREADY_VOTED, "already-voted ID (lowercase, spaces) detected");
        check(svc.verify("BRGY-2026-00113") == VerifyResult.ELIGIBLE, "fresh voter is eligible");

        List<ElectionService.Result> cap = svc.results(Position.CAPTAIN);
        check(cap.get(0).candidate.getName().equals("Maria Santos") && cap.get(0).votes == 6
                && cap.get(1).votes == 4 && cap.get(2).votes == 2, "seeded captain tally 6/4/2 ranked");

        Voter v = svc.findVoter("BRGY-2026-00113");
        svc.castBallot(v, ballot("C01", "C05", "C07"));
        check(svc.ballotsCast() == 13 && v.hasVoted(), "valid ballot recorded, voter flagged");
        check(svc.results(Position.CAPTAIN).get(0).votes == 7, "captain tally updated to 7");
        check(svc.verify("BRGY-2026-00113") == VerifyResult.ALREADY_VOTED, "same voter blocked afterwards");

        try { svc.castBallot(v, ballot("C01", "C05", "C07")); check(false, "double vote rejected"); }
        catch (IllegalStateException e) { check(svc.ballotsCast() == 13, "double vote rejected, count unchanged"); }

        Voter v2 = svc.findVoter("BRGY-2026-00114");
        Ballot partial = new Ballot();
        partial.choose(Position.CAPTAIN, "C01");
        try { svc.castBallot(v2, partial); check(false, "incomplete ballot rejected"); }
        catch (IllegalArgumentException e) { check(svc.ballotsCast() == 13 && !v2.hasVoted(), "incomplete ballot rejected"); }
        try { svc.castBallot(v2, ballot("C04", "C05", "C07")); check(false, "wrong-position candidate rejected"); }
        catch (IllegalArgumentException e) { check(svc.ballotsCast() == 13 && !v2.hasVoted(), "candidate from wrong position rejected"); }

        ElectionService reloaded = new ElectionService(new FileDataStore(dir));
        check(reloaded.ballotsCast() == 13 && reloaded.findVoter("BRGY-2026-00113").hasVoted(), "data persisted to files and reloaded");

        svc.castBallot(v2, ballot("C02", "C05", "C09"));
        List<ElectionService.Result> sk = svc.results(Position.SK_CHAIR);
        check(svc.isTie(sk) && sk.get(0).votes == 5, "tie for first place detected (SK 5 vs 5)");

        svc.addCandidate(Position.KAGAWAD, "  New   Person, Jr ", "");
        Candidate added = svc.candidatesFor(Position.KAGAWAD).get(3);
        check(added.getId().equals("C10") && added.getName().equals("New Person Jr") && added.getParty().equals("Independent"), "add candidate cleaned, default party");
        try { svc.addCandidate(Position.KAGAWAD, "new person jr", "X"); check(false, "duplicate rejected"); }
        catch (IllegalArgumentException e) { check(true, "duplicate candidate rejected"); }
        try { svc.addCandidate(Position.CAPTAIN, "  ", "X"); check(false, "blank name rejected"); }
        catch (IllegalArgumentException e) { check(true, "blank candidate name rejected"); }
        svc.updateCandidate("C01", "Maria S. Santos", "Lakas Barangay");
        check(svc.results(Position.CAPTAIN).get(0).candidate.getName().equals("Maria S. Santos")
                && svc.results(Position.CAPTAIN).get(0).votes == 7, "edited name keeps its votes");
        check(new ElectionService(new FileDataStore(dir)).candidatesFor(Position.KAGAWAD).size() == 4, "candidate changes persisted");

        check(svc.login("admin", "admin123") != null && svc.login("admin", "wrong") == null && svc.login("", "") == null, "admin login accepts only correct credentials");
        Person p = svc.login("admin", "admin123");
        check(p.getRole().equals("Administrator") && svc.findVoter("BRGY-2026-00101").getRole().equals("Voter"), "polymorphic getRole()");

        svc.resetDemo();
        check(svc.ballotsCast() == 12 && !svc.findVoter("BRGY-2026-00113").hasVoted() && svc.candidatesFor(Position.KAGAWAD).size() == 3, "reset restores sample data");

        Files.write(dir.resolve("voters.csv"), "garbage".getBytes(StandardCharsets.UTF_8));
        boolean failed = false;
        try { new ElectionService(new FileDataStore(dir)); } catch (RuntimeException e) { failed = true; }
        check(failed, "damaged data file is detected on load");
        FileDataStore fixer = new FileDataStore(dir);
        fixer.resetToSampleData();
        check(new ElectionService(fixer).registeredVoters() == 20, "damaged files recover after reset");

        System.out.println(failures == 0 ? "\nALL CHECKS PASSED" : "\n" + failures + " CHECK(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }
}

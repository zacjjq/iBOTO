package iboto.data;

import iboto.model.Ballot;
import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;

import java.util.ArrayList;
import java.util.List;

/** Predictable demo data: 20 voters (first 12 already voted), 9 candidates, 12 recorded ballots. */
public final class SampleData {
    private static final String[] NAMES = {
        "Juan Dizon", "Carmen Ramos", "Pedro Aquino", "Luz Fernandez", "Rico Castillo",
        "Nena Salazar", "Andres Ocampo", "Teresa Pascual", "Mario Domingo", "Elena Cruz",
        "Ben Alvarez", "Rosa Manalo", "Dante Soriano", "Grace Evangelista", "Leo Magsaysay",
        "Cathy Robles", "Victor Santiago", "Joy Hernandez", "Nico Tolentino", "Pia Valdez"};
    private static final int ALREADY_VOTED = 12;

    private SampleData() { }

    public static List<Voter> voters() {
        List<Voter> list = new ArrayList<>();
        for (int i = 0; i < NAMES.length; i++) {
            list.add(new Voter(String.format("BRGY-2026-%05d", 101 + i), NAMES[i], i < ALREADY_VOTED));
        }
        return list;
    }

    public static List<Candidate> candidates() {
        List<Candidate> list = new ArrayList<>();
        list.add(new Candidate("C01", Position.CAPTAIN, "Maria Santos", "Independent"));
        list.add(new Candidate("C02", Position.CAPTAIN, "Jose Reyes", "Team Bayanihan"));
        list.add(new Candidate("C03", Position.CAPTAIN, "Ana Dela Cruz", "Lakas Barangay"));
        list.add(new Candidate("C04", Position.KAGAWAD, "Ramon Villanueva", "Team Bayanihan"));
        list.add(new Candidate("C05", Position.KAGAWAD, "Liza Mendoza", "Independent"));
        list.add(new Candidate("C06", Position.KAGAWAD, "Carlo Bautista", "Lakas Barangay"));
        list.add(new Candidate("C07", Position.SK_CHAIR, "Paolo Garcia", "Independent"));
        list.add(new Candidate("C08", Position.SK_CHAIR, "Bea Navarro", "Team Bayanihan"));
        list.add(new Candidate("C09", Position.SK_CHAIR, "Miguel Torres", "Lakas Barangay"));
        return list;
    }

    /** Captain: Santos 6, Reyes 4, Dela Cruz 2. Kagawad: 5/4/3. SK: Navarro 5, Torres 4, Garcia 3. */
    public static List<Ballot> ballots() {
        int[] captain = {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3};
        int[] kagawad = {4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6};
        int[] sk      = {8, 8, 8, 8, 8, 9, 9, 9, 9, 7, 7, 7};
        List<Ballot> list = new ArrayList<>();
        for (int i = 0; i < ALREADY_VOTED; i++) {
            Ballot b = new Ballot();
            b.choose(Position.CAPTAIN, String.format("C%02d", captain[i]));
            b.choose(Position.KAGAWAD, String.format("C%02d", kagawad[i]));
            b.choose(Position.SK_CHAIR, String.format("C%02d", sk[i]));
            list.add(b);
        }
        return list;
    }
}

package iboto.model;

import java.util.EnumMap;
import java.util.Map;

/** One anonymous ballot: the chosen candidate id per position. It never records who cast it. */
public class Ballot {
    private final Map<Position, String> picks = new EnumMap<>(Position.class);

    public void choose(Position position, String candidateId) { picks.put(position, candidateId); }

    public String choiceFor(Position position) { return picks.get(position); }

    public boolean isComplete() {
        for (Position p : Position.values()) {
            if (picks.get(p) == null) return false;
        }
        return true;
    }

    /** One line per ballot: captainId,kagawadId,skId */
    public String toCsv() {
        StringBuilder sb = new StringBuilder();
        for (Position p : Position.values()) {
            if (sb.length() > 0) sb.append(',');
            sb.append(picks.get(p));
        }
        return sb.toString();
    }

    public static Ballot fromCsv(String line) {
        String[] parts = line.split(",");
        Position[] all = Position.values();
        Ballot b = new Ballot();
        for (int i = 0; i < all.length; i++) b.choose(all[i], parts[i].trim());
        return b;
    }
}

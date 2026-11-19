package iboto.data;

import iboto.model.Ballot;
import iboto.model.Candidate;
import iboto.model.Position;
import iboto.model.Voter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** FILE HANDLING: keeps everything in plain CSV files inside one folder (voters, candidates, ballots). */
public final class FileDataStore implements DataStore {
    private final Path votersFile;
    private final Path candidatesFile;
    private final Path ballotsFile;

    public FileDataStore(Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        votersFile = folder.resolve("voters.csv");
        candidatesFile = folder.resolve("candidates.csv");
        ballotsFile = folder.resolve("ballots.csv");
        if (!Files.exists(votersFile) || !Files.exists(candidatesFile) || !Files.exists(ballotsFile)) {
            resetToSampleData();
        }
    }

    private List<String> read(Path file) {
        try {
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
            return lines;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void write(Path file, List<String> lines) {
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<Voter> loadVoters() {
        List<Voter> list = new ArrayList<>();
        for (String line : read(votersFile)) {
            String[] f = line.split(",", -1);
            list.add(new Voter(f[0], f[1], Boolean.parseBoolean(f[2])));
        }
        return list;
    }

    @Override
    public void saveVoters(List<Voter> voters) {
        List<String> lines = new ArrayList<>();
        for (Voter v : voters) lines.add(v.getId() + "," + v.getName() + "," + v.hasVoted());
        write(votersFile, lines);
    }

    @Override
    public List<Candidate> loadCandidates() {
        List<Candidate> list = new ArrayList<>();
        for (String line : read(candidatesFile)) {
            String[] f = line.split(",", -1);
            list.add(new Candidate(f[0], Position.valueOf(f[1]), f[2], f[3]));
        }
        return list;
    }

    @Override
    public void saveCandidates(List<Candidate> candidates) {
        List<String> lines = new ArrayList<>();
        for (Candidate c : candidates) {
            lines.add(c.getId() + "," + c.getPosition().name() + "," + c.getName() + "," + c.getParty());
        }
        write(candidatesFile, lines);
    }

    @Override
    public List<Ballot> loadBallots() {
        List<Ballot> list = new ArrayList<>();
        for (String line : read(ballotsFile)) list.add(Ballot.fromCsv(line));
        return list;
    }

    @Override
    public void saveBallots(List<Ballot> ballots) {
        List<String> lines = new ArrayList<>();
        for (Ballot b : ballots) lines.add(b.toCsv());
        write(ballotsFile, lines);
    }

    @Override
    public void resetToSampleData() {
        saveVoters(SampleData.voters());
        saveCandidates(SampleData.candidates());
        saveBallots(SampleData.ballots());
    }
}

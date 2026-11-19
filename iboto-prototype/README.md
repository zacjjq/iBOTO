# iBOTO! - Barangay Digital Voting System (academic prototype)

Java Swing desktop app, OOP design, plain CSV files for storage. Sample data only.

## Run
Needs a JDK (8 or newer). From this folder:
- Windows: double-click `run.bat`
- Mac / Linux: `./run.sh`
- or manually: `javac -d out $(find src -name '*.java')` then `java -cp out iboto.ui.IbotoApp`
- IDE (IntelliJ / VS Code): open this folder, mark `src` as sources, run `iboto.ui.IbotoApp`.
  **Set the working directory to this folder** so the `data/` folder is created here.

The first launch creates `data/` (voters.csv, candidates.csv, ballots.csv) with sample data.
Optional rule check (no window): compile `test/SelfCheck.java` with the rest, then `java -cp out SelfCheck`.

## Sample data
- 20 voters `BRGY-2026-00101` ... `00120`. The first 12 have already voted; `00113`-`00120` are free.
- 9 candidates (3 each for Barangay Captain, Kagawad, SK Chairperson), 12 recorded ballots.
- Starting results: Captain Santos 6 / Reyes 4 / Dela Cruz 2 - Kagawad 5 / 4 / 3 - SK Navarro 5 / Torres 4 / Garcia 3.
- Admin login: `admin` / `admin123` (shown on the screen).
- The login screen has "Demo" shortcuts that only fill the ID box: Eligible voter, Already voted, Not eligible.

## Demo script (about 5 minutes)
1. **Home** -> "I'm a Voter".
2. **Invalid ID:** click *Not eligible* -> Verify ID -> "Barangay ID not eligible" -> Try Again.
3. **Duplicate prevention:** click *Already voted* -> Verify ID -> "You have already voted" -> Back to Home.
4. **Normal vote:** *Eligible voter* -> Verify ID -> Scan Face (simulated, 1.5 s) -> pick one candidate per position.
   Try *Review Ballot* with a position empty to show the validation message.
5. **Review** -> Back to Ballot (choices are kept) -> Review Ballot -> **Submit Vote** -> success screen -> Log Out.
6. **Same voter again:** the ID you just used now shows "You have already voted".
7. **Face mismatch (optional):** Eligible voter -> Verify -> click *Face does not match* -> failure screen.
8. **Admin** -> log in -> **Results** show ballots cast 13 of 20 and your candidate's count went up by one.
   **Voters** shows who has voted (never how they voted). **Candidates**: add or edit one, then check the ballot screen.
9. **Reset Demo Data** (top right, asks to confirm) puts everything back to the sample state for the next run.

## OOP map (where to point during questions)
| Concept | Where |
|---|---|
| Abstraction | `model/Person` (abstract), `data/DataStore` (interface) |
| Inheritance | `Voter` and `Admin` extend `Person`; every screen extends `ui/Screen` |
| Polymorphism | `getRole()` is overridden in `Voter` / `Admin`; `Person.describe()` uses it; `FileDataStore` can be swapped for any other `DataStore` |
| Encapsulation | private fields with getters; `Voter.markVoted()` is the only way to change the voted flag; the UI never touches files |
| Separation of layers | `model` (data objects) - `data` (files) - `service/ElectionService` (all rules) - `ui` (screens) |

## What is simulated / not real
- **Face verification is simulated**: no camera, no image processing. The button waits and reports a pre-decided result.
- Admin password is hard-coded and files are plain text: **no real security**. Not for real elections.
- Single computer, one voter at a time, no network. One choice per position (real Kagawad voting allows several).
- Ballots are stored without voter IDs, so a vote cannot be traced back to a voter in the files.
- The mobile layout from the design PDF is not implemented (desktop app only).

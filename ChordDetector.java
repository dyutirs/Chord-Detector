
import java.util.*;

public class ChordDetector {

    static String[] NOTES = {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

    enum ChordFamily {
        MAJORX, MINORX, DIMX, HALFDIMX, MINMAJX, AUGX, ADDX, MINADDX, SUSX, ALTEREDX, XADDY
    }

    static Map<ChordFamily, String[]> FAMILY_NAMES = new HashMap<>();
    static {
        FAMILY_NAMES.put(ChordFamily.MAJORX, new String[]{"maj","maj7","maj9","maj11","maj13"});
        FAMILY_NAMES.put(ChordFamily.MINORX, new String[]{"min","min7","min9","min11","min13"});
        FAMILY_NAMES.put(ChordFamily.DIMX, new String[]{"dim","dim7"});
        FAMILY_NAMES.put(ChordFamily.HALFDIMX, new String[]{"m7b5"});
        FAMILY_NAMES.put(ChordFamily.MINMAJX, new String[]{"mMaj7"});
        FAMILY_NAMES.put(ChordFamily.AUGX, new String[]{"aug","aug7","aug9","aug11","aug13"});
        FAMILY_NAMES.put(ChordFamily.ADDX, new String[]{"add9","add11","add13"});
        FAMILY_NAMES.put(ChordFamily.MINADDX, new String[]{"minadd9"});
        FAMILY_NAMES.put(ChordFamily.SUSX, new String[]{"sus2","sus4"});
        FAMILY_NAMES.put(ChordFamily.ALTEREDX, new String[]{"7b5","7#5","7b9","7#9","7#11"});
        FAMILY_NAMES.put(ChordFamily.XADDY, new String[]{"XaddY"}); // dynamic
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.print("Enter notes (C D# G etc.) or 'exit': ");
            String line = sc.nextLine();
            if(line.equalsIgnoreCase("exit")) break;

            String[] noteNames = line.trim().split("\\s+");
            List<Integer> noteValues = new ArrayList<>();
            for(String note : noteNames){
                int idx = Arrays.asList(NOTES).indexOf(note);
                if(idx==-1){
                    System.out.println("Invalid note: " + note);
                    noteValues.clear();
                    break;
                }
                noteValues.add(idx);
            }
            if(noteValues.isEmpty()) continue;

            List<String> detected = detectChord(noteValues);
            System.out.println("Detected Chord(s): " + (detected.isEmpty() ? "[No chord matched]" : detected));
        }
    }

    static List<String> detectChord(List<Integer> notes){
        Set<String> matches = new LinkedHashSet<>();

        ChordFamily[] families = ChordFamily.values();

        for(int i=0;i<notes.size();i++){
            int root = notes.get(i);
            Set<Integer> normalizedInput = new HashSet<>();
            for(int n : notes){
                normalizedInput.add((n - root + 12) % 12);
            }

            for(ChordFamily family : families){
                String[] variants = FAMILY_NAMES.get(family);
                for(int v=0; v<variants.length; v++){
                    int[] chordIntervals = generateIntervals(family, v); // dynamic generation
                    if(chordIntervals.length != normalizedInput.size()) continue;

                    Set<Integer> chordSet = new HashSet<>();
                    for(int interval : chordIntervals){
                        chordSet.add(interval % 12);
                    }

                    if(chordSet.equals(normalizedInput)){
                        String chordName = NOTES[root] + variants[v];
                        if(i==0) return Arrays.asList(chordName); // prefer first note as root
                        matches.add(chordName);
                    }
                }
            }
        }
        return new ArrayList<>(matches);
    }

    static int[] generateIntervals(ChordFamily family, int variant){
        // Generate intervals dynamically for all families
        switch(family){
            case MAJORX:
                return generateIntervalsFromBase(new int[]{0,4,7}, variant, new int[]{10,14,17,21});
            case MINORX:
                return generateIntervalsFromBase(new int[]{0,3,7}, variant, new int[]{10,14,17,21});
            case DIMX:
                return generateIntervalsFromBase(new int[]{0,3,6}, variant, new int[]{9});
            case HALFDIMX:
                return new int[]{0,3,6,10};
            case MINMAJX:
                return new int[]{0,3,7,11};
            case AUGX:
                return generateIntervalsFromBase(new int[]{0,4,8}, variant, new int[]{11,14,17,21});
            case ADDX:
                return generateIntervalsFromBase(new int[]{0,4,7}, variant, new int[]{14,17,21});
            case MINADDX:
                return generateIntervalsFromBase(new int[]{0,3,7}, variant, new int[]{14});
            case SUSX:
                return new int[]{0,(variant==1?2:5),7};
            case ALTEREDX:
                switch(variant){
                    case 0: return new int[]{0,4,7,10}; // basic 7
                    case 1: return new int[]{0,4,6,10}; // 7b5
                    case 2: return new int[]{0,4,8,10}; // 7#5
                    case 3: return new int[]{0,4,7,10,14}; // 7b9
                    case 4: return new int[]{0,4,7,10,15}; // 7#9
                    case 5: return new int[]{0,4,7,10,18}; // 7#11
                }
            case XADDY:
                switch(variant){
                    case 0: return new int[]{0,4,7,14}; // add9
                    case 1: return new int[]{0,3,7,14}; // minadd9
                    case 2: return new int[]{0,4,7,9,14}; // 6add9
                    case 3: return new int[]{0,7,14}; // 5add9
                }
        }
        return new int[0];
    }

    static int[] generateIntervalsFromBase(int[] base, int variant, int[] extra){
        int[] intervals = Arrays.copyOf(base, base.length + variant);
        for(int i=0;i<variant;i++){
            intervals[base.length + i] = extra[i];
        }
        return intervals;
    }
}

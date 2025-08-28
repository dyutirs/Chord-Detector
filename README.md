# Chord Detector App  

A Java-based **Chord Detection Tool** with three powerful modes:  

1. **Manual Note Entry** – Enter notes directly and instantly get the chord name.  
2. **Guitar Fretboard Mode** – Select strings and frets on a virtual guitar fretboard to identify chords.  
3. **Audio-to-Chord Mode** – Play or sing chords, and the app will detect frequencies (via **Pitchy API**), convert them to notes, and fi
d the correct chord using a custom **Chord Name Finding Algorithm**.  

---

##  Features  
- Written in **Java**  
- **Pitchy API** integration for accurate frequency-to-note conversion  
- Custom algorithm for chord name detection  
- Interactive **guitar fretboard UI**  
- Works with both **manual input** and **live audio input**  

---

## Project Structure  
├── guitar.png/ #fretboard UI
├── chord.java # Core chord logic
├── ChordDetector.java # Chord detection algorithms
├── ChordServer.java # Backend handling
├── index.html # Main UI
├── backend_test.html # Test interface
├── frontend_debug.html # Debugging UI
├── pitchy.esm.js # Pitchy API integration
)


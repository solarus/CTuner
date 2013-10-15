package org.tunna.ctuner;

import android.util.Pair;
import java.util.ArrayList;

public class Util {

    enum Note {
        A, AS, B, C, CS, D, DS, E, F, FS, G, GS;

        public Note next() {
            if (this == Note.GS) {
                return Note.A;
            }

            return values()[ordinal() + 1];
        }

        public Note prev() {
            if (this == Note.A) {
                return Note.GS;
            }

            return values()[ordinal() - 1];
        }

        private static String[] stringVals =
            new String[] { "A", "A#", "B", "C", "C#", "D",
                           "D#", "E", "F", "F#", "G", "G#" };

        public String toString() {
            return stringVals[ordinal()];
        }
    }

    public static void guessNote(float freq, NoteGuessResult guess) {
        float lowC = (float) 27.5 * (float) Math.pow(2, 1.0/4.0);

        int octave = 1;

        Note note = Note.CS;

        final float a12 = (float) Math.pow(2, 1.0/12.0);

        float prevFreq = lowC;
        float noteFreq = lowC * a12;
        while (noteFreq < freq) {
            prevFreq  = noteFreq;
            noteFreq *= a12;
            note      = note.next();
            if (note == Note.A) {
                ++octave;
            }
        }

        // Choose previous note if that one was a better match
        if (freq - prevFreq < noteFreq - freq) {
            noteFreq = prevFreq;
            prevFreq /= a12;
            note = note.prev();
            if (note == Note.GS) {
                --octave;
            }
        }

        float offset = freq - noteFreq;
        float offsetRatio = 2 * offset / (noteFreq - prevFreq);
        guess.note = note;
        guess.octave = octave;
        guess.offset = offset;
        guess.offsetRatio = offsetRatio;
        guess.realPitch = noteFreq;
    }

    public static class NoteGuessResult {
        public Note note;
        public int octave;
        public float offset;
        public float offsetRatio;
        public float realPitch;

        public NoteGuessResult() {
            this(Note.C, 1, 0, 0, 0);
        }

        public NoteGuessResult(Note n, int oc, float of, float ofr, float real) {
            note        = n;
            octave      = oc;
            offset      = of;
            offsetRatio = ofr;
            realPitch   = real;
        }

        public String toString() {
            return note.toString() + octave;
        }
    }

}

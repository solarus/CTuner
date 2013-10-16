package org.tunna.ctuner;

import android.graphics.Paint;
import android.util.Pair;
import java.util.ArrayList;

public class Util {

    public static int green = -1;
    public static int gray = -1;
    public static int drawColor = -1;

    enum Note {
        A(false), AS(true), B(false), C(false), CS(true), D(false),
        DS(true), E(false), F(false), FS(true), G(false), GS(true);

        public final boolean isSharp;

        private Note(boolean isSharp) {
            this.isSharp = isSharp;
        }

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

        public String noteLetter() {
            if (isSharp) {
                return prev().toString();
            }
            else {
                return toString();
            }
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

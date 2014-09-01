package org.tunna.ctuner;

import org.tunna.ctuner.Util.*;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final boolean ON_GENY = false;

    private static final int SAMPLERATE        = 11025;
    private static final int NUM_CHANNELS      = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int BUFFER_SIZE    = 4096;
    private static final int BUFFER_OVERLAY = BUFFER_SIZE * 3/4;

    private static final int FRAMERATE    = 60;
    private static final int UPDATE_DELAY = 1000/FRAMERATE;

    private boolean runUpdateThread = true;
    private AudioRecord recorder    = null;
    private boolean startedRecording = false;
    private boolean isRecording = false;
    private TextView freqTV = null;
    private TextView noteTV = null;
    private TextView noteSharpTV = null;
    private TextView noteOctaveTV = null;
    private HorizontalBarView barView = null;
    private FastYin yin = null;
    private long lastUpdateTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Util.green = getResources().getColor(R.color.green);
        Util.gray = getResources().getColor(R.color.gray);
        Util.drawColor = Util.gray;

        double yinThreshold = 0.3;
        yin = new FastYin(SAMPLERATE, BUFFER_SIZE, yinThreshold);

        freqTV = (TextView) findViewById(R.id.freq);
        noteTV = (TextView) findViewById(R.id.note);
        noteSharpTV = (TextView) findViewById(R.id.note_sharp);
        noteOctaveTV = (TextView) findViewById(R.id.note_octave);
        barView = (HorizontalBarView) findViewById(R.id.bar_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRecording();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        runUpdateThread = false;
    }

    private float pitch = 220;
    private void startRecording() {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLERATE, NUM_CHANNELS, RECORDER_ENCODING);

        final int bufferSize = Math.max(minBufferSize, BUFFER_SIZE);

        Log.d("### bufferSize = " + bufferSize);

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                   SAMPLERATE,
                                   NUM_CHANNELS,
                                   RECORDER_ENCODING,
                                   bufferSize);
        recorder.startRecording();
        isRecording = true;

        new Thread() {
            public void run() {
                final short[] sData = new short[BUFFER_SIZE];
                final float[] fData = new float[BUFFER_SIZE];

                final int diff = bufferSize - BUFFER_OVERLAY;
                // pitch = 0;

                // This loop will be correct after 3 rounds because of
                // the BUFFER_OVERLAY offset
                while (isRecording) {
                    recorder.read(sData, BUFFER_OVERLAY, diff);

                    for (int i = BUFFER_OVERLAY; i < diff; ++i) {
                        fData[i] = (float) sData[i];
                    }

                    float currentPitch = yin.getPitch(fData).getPitch();
                    if (currentPitch != -1 && currentPitch > 30) {
                        pitch = currentPitch;
                    }

                    runOnUiThread(new Runnable() {
                            public void run() {
                                updateNote(pitch);
                            }
                        });

                    for (int i = 0; i < BUFFER_OVERLAY; ++i) {
                        sData[i] = sData[i + diff];
                        fData[i] = (float) sData[i + diff];
                    }
                }
            }
        }.start();
    }

    private final NoteGuessResult guess = new NoteGuessResult();
    private synchronized void updateNote(final float pitch) {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime < currentTime - UPDATE_DELAY) {
            Util.guessNote(pitch, guess);

            updateColor(guess.offsetRatio);

            noteTV.setText(guess.note.noteLetter());
            String sharpText = guess.note.isSharp ? "#" : "";
            noteSharpTV.setText(sharpText);
            noteOctaveTV.setText(Integer.toString(guess.octave));
            freqTV.setText(String.format("%.1f (%.1f)", pitch, guess.realPitch));

            barView.setLength(guess.offsetRatio);

            lastUpdateTime = currentTime;
        }
    }

    private void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void updateColor(float offsetRatio) {
        Util.drawColor = Math.abs(offsetRatio) <= 0.015 ? Util.green : Util.gray;
        setTVColor(Util.drawColor);
        barView.setColor(Util.drawColor);
    }

    private void setTVColor(int color) {
        noteTV.setTextColor(color);
        noteSharpTV.setTextColor(color);
        noteOctaveTV.setTextColor(color);
    }

}

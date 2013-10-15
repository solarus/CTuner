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

    private static final boolean ON_GENY = true;

    private static final int SAMPLERATE        = ON_GENY ? 8000 : 44100;
    private static final int NUM_CHANNELS      = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final int BUFFER_SIZE    = 4096;
    private static final int BUFFER_OVERLAY = BUFFER_SIZE * 3/4;

    private boolean runUpdateThread = true;
    private AudioRecord recorder    = null;
    private boolean startedRecording = false;
    private boolean isRecording = false;
    private TextView freqTV = null;
    private TextView noteTV = null;
    private FastYin yin = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        double yinThreshold = 0.3;
        yin = new FastYin(SAMPLERATE, BUFFER_SIZE, yinThreshold);

        freqTV = (TextView) findViewById(R.id.freq);
        noteTV = (TextView) findViewById(R.id.note);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRecording();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
        runUpdateThread = false;
    }

    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                   SAMPLERATE,
                                   NUM_CHANNELS,
                                   RECORDER_ENCODING,
                                   BUFFER_SIZE);
        recorder.startRecording();
        isRecording = true;

        new Thread() {
            public void run() {
                final short[] sData = new short[BUFFER_SIZE];
                final float[] fData = new float[BUFFER_SIZE];

                // This loop will be correct after 3 rounds because of
                // the BUFFER_OVERLAY offset
                while (isRecording) {
                    recorder.read(sData, BUFFER_OVERLAY, BUFFER_SIZE - BUFFER_OVERLAY);

                    short2Float(sData, fData);

                    final Float pitch = yin.getPitch(fData).getPitch();

                    runOnUiThread(new Runnable() {
                            public void run() {
                                NoteGuessResult guess = Util.guessNote(pitch);
                                noteTV.setText(guess.toString());
                                freqTV.setText(String.format("%.1f", pitch));
                            }
                        });

                    for (int i = 0; i < BUFFER_OVERLAY; ++i) {
                        sData[i] = sData[i + BUFFER_SIZE - BUFFER_OVERLAY];
                    }
                }
            }
        }.start();
    }

    private void short2Float(short[] sData, float[] fData) {
        int n = sData.length;
        for (int i = 0; i < n; ++i) {
            fData[i] = (float) sData[i];
        }
    }

    private void stopRecording() {
        isRecording = false;
        recorder.stop();
        recorder.release();
        recorder = null;
    }

}

package com.example.avtovideoeditor;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayDeque;
import java.util.Arrays;

public class BPMCounterActivity extends AppCompatActivity {
    private static final long TIMING_DEQUE_SIZE = 16;
    private static final long MEDIAN_DEQUE_SIZE = 9;
    private Button bpmButton;
    private long lastButtonPushTime;
    private ArrayDeque<Long> timingValuesDeque;
    private ArrayDeque<Float> bpmValuesDeque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpm_counter);

        lastButtonPushTime = 0;
        timingValuesDeque = new ArrayDeque<>();
        bpmValuesDeque = new ArrayDeque<>();

        bpmButton = (Button) findViewById(R.id.bpm_button);
        bpmButton.setOnTouchListener(onBpmButtonTouch);
        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(onResetButtonClick);
        // For a longer title in the title bar.
        // (From API level 11 on, getActionBar can be used)
        getSupportActionBar().setTitle(R.string.title_activity_main);
    }

    private final View.OnTouchListener onBpmButtonTouch = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if (motionEvent.getAction() != MotionEvent.ACTION_DOWN)
                return false;

            long currentTime = System.currentTimeMillis();
            if (lastButtonPushTime == 0) {
                lastButtonPushTime = currentTime;
                return true;
            }
            Long timeElapsed = currentTime - lastButtonPushTime;
            lastButtonPushTime = currentTime;

            // Detect missed beat: current measurement appears twice the last one
            if (timingValuesDeque.size() > 0 && timeElapsed > 1.75 * timingValuesDeque.getFirst() &&
                    timeElapsed < 2.3 * timingValuesDeque.getFirst()) {
                // Insert half the value twice to fill in the missed beat
                timeElapsed /= 2;
                timingValuesDeque.addFirst(timeElapsed);
            }
            timingValuesDeque.addFirst(timeElapsed);
            while (timingValuesDeque.size() > TIMING_DEQUE_SIZE)
                timingValuesDeque.removeLast();

            float weightedAverageTime = decayingWeightedAverageFromDeque(timingValuesDeque);
            float bpmEstimate = 60000.0F / weightedAverageTime;

            // Calculate median of the set of previous weighted averages
            bpmValuesDeque.addFirst(bpmEstimate);
            while (bpmValuesDeque.size() > MEDIAN_DEQUE_SIZE)
                bpmValuesDeque.removeLast();

            long bpmMedian = Math.round(medianFromFloatDeque(bpmValuesDeque));

            bpmButton.setText(String.format("%.1f", bpmEstimate) + "\n" + getString(R.string.median)
                    + ": " + String.valueOf(bpmMedian));
            return true;
        }
    };

    private final View.OnClickListener onResetButtonClick = new View.OnClickListener() {
        public void onClick(View v) {
            timingValuesDeque.clear();
            bpmValuesDeque.clear();
            lastButtonPushTime = 0;
            bpmButton.setText(R.string.bpm_hitme);
        }
    };

    private float decayingWeightedAverageFromDeque(ArrayDeque<Long> deque) {
        float weightedAverage = 0, weight = 1, totalWeight = 0;
        int valuesAdded = 0;
        // Weighted average: exponentially decrease the weight after the third value
        for (Long value : deque) {
            weightedAverage += value * weight;
            totalWeight += weight;
            valuesAdded++;
            if (valuesAdded > 3)
                weight *= .8;
        }
        if (totalWeight == 0)
            return weightedAverage;
        return weightedAverage / totalWeight;
    }

    private float medianFromFloatDeque(ArrayDeque<Float> deque) {
        float bpmValuesArray[] = new float[deque.size()];
        int i = 0;
        for (Float value : deque) {
            bpmValuesArray[i] = value;
            i++;
        }
        Arrays.sort(bpmValuesArray);
        int arrayLength = bpmValuesArray.length;
        if (arrayLength % 2 == 0)
            return (bpmValuesArray[arrayLength / 2] + bpmValuesArray[arrayLength / 2 - 1]) / 2;
        else
            return bpmValuesArray[arrayLength / 2];
    }
}

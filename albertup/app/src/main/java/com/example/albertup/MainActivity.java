package com.example.albertup;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int DAILY_GOAL = 100;
    private static final int MIN_REPS = 1;
    private static final int MAX_REPS = 200;

    private String currentExercise;
    private boolean isPushups = true;
    private int currentReps = 12;

    private final ArrayList<String> loggedExercises = new ArrayList<>();
    private final ArrayList<Integer> loggedReps = new ArrayList<>();
    private int bestPushups = 0;
    private int bestPullups = 0;

    private Button btnPushups;
    private Button btnPullups;
    private Button btnMinus;
    private Button btnPlus;
    private Button btnLog;
    private TextView tvRepCount;
    private TextView tvTodayRepsValue;
    private TextView tvTodaySetsValue;
    private TextView tvDailyGoal;
    private TextView tvTodaysSetsTitle;
    private TextView tvPrPushValue;
    private TextView tvPrPullValue;
    private TextView tvMotivation;
    private LinearLayout setsList;
    private ImageView ivEmpty;
    private ProgressBar progressDaily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentExercise = getString(R.string.pushups);

        btnPushups = findViewById(R.id.btnPushups);
        btnPullups = findViewById(R.id.btnPullups);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnLog = findViewById(R.id.btnLog);
        tvRepCount = findViewById(R.id.tvRepCount);
        tvTodayRepsValue = findViewById(R.id.tvTodayRepsValue);
        tvTodaySetsValue = findViewById(R.id.tvTodaySetsValue);
        tvDailyGoal = findViewById(R.id.tvDailyGoal);
        tvTodaysSetsTitle = findViewById(R.id.tvTodaysSetsTitle);
        tvPrPushValue = findViewById(R.id.tvPrPushValue);
        tvPrPullValue = findViewById(R.id.tvPrPullValue);
        tvMotivation = findViewById(R.id.tvMotivation);
        setsList = findViewById(R.id.setsList);
        ivEmpty = findViewById(R.id.ivEmpty);
        progressDaily = findViewById(R.id.progressDaily);

        if (savedInstanceState != null) {
            ArrayList<String> savedExercises = savedInstanceState.getStringArrayList("exercises");
            ArrayList<Integer> savedReps = savedInstanceState.getIntegerArrayList("reps");
            if (savedExercises != null) {
                loggedExercises.addAll(savedExercises);
            }
            if (savedReps != null) {
                loggedReps.addAll(savedReps);
            }
            isPushups = savedInstanceState.getBoolean("isPushups", true);
            currentReps = savedInstanceState.getInt("currentReps", 12);
            bestPushups = savedInstanceState.getInt("bestPushups", 0);
            bestPullups = savedInstanceState.getInt("bestPullups", 0);
            currentExercise = isPushups ? getString(R.string.pushups) : getString(R.string.pullups);
            for (int i = 0; i < loggedExercises.size(); i++) {
                addSetRowView(i);
            }
        }

        btnPushups.setOnClickListener(v -> {
            isPushups = true;
            currentExercise = getString(R.string.pushups);
            updateSelectorUI();
            updateStepperUI();
        });

        btnPullups.setOnClickListener(v -> {
            isPushups = false;
            currentExercise = getString(R.string.pullups);
            updateSelectorUI();
            updateStepperUI();
        });

        btnPlus.setOnClickListener(v -> {
            if (currentReps < MAX_REPS) {
                currentReps++;
                updateStepperUI();
            }
        });

        btnMinus.setOnClickListener(v -> {
            if (currentReps > MIN_REPS) {
                currentReps--;
                updateStepperUI();
            }
        });

        btnLog.setOnClickListener(v -> logSet());

        updateSelectorUI();
        updateStepperUI();
        updateTotals();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("exercises", new ArrayList<>(loggedExercises));
        outState.putIntegerArrayList("reps", new ArrayList<>(loggedReps));
        outState.putBoolean("isPushups", isPushups);
        outState.putInt("currentReps", currentReps);
        outState.putInt("bestPushups", bestPushups);
        outState.putInt("bestPullups", bestPullups);
    }

    private void logSet() {
        loggedExercises.add(currentExercise);
        loggedReps.add(currentReps);
        boolean isNewBest = false;
        if (isPushups && currentReps > bestPushups) {
            bestPushups = currentReps;
            isNewBest = true;
        }
        if (!isPushups && currentReps > bestPullups) {
            bestPullups = currentReps;
            isNewBest = true;
        }
        addSetRowView(loggedExercises.size() - 1);
        updateTotals();
        if (isNewBest) {
            Snackbar.make(findViewById(R.id.main),
                    getString(R.string.new_pr_message),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    private void addSetRowView(int index) {
        String exercise = loggedExercises.get(index);
        int reps = loggedReps.get(index);
        int setNumber = index + 1;

        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        if (index > 0) {
            cardParams.topMargin = getResources().getDimensionPixelSize(R.dimen.spacing_small);
        }
        card.setLayoutParams(cardParams);
        card.setRadius(getResources().getDimension(R.dimen.card_radius));
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface_card));
        card.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.card_stroke));
        card.setStrokeColor(ContextCompat.getColor(this, R.color.divider));
        int padding = getResources().getDimensionPixelSize(R.dimen.set_row_padding);
        card.setContentPadding(padding, padding, padding, padding);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView text = new TextView(this);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        text.setLayoutParams(textParams);
        text.setText(getString(R.string.set_row_format, setNumber, exercise, reps));
        text.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.body_text_size));

        row.addView(text);
        card.addView(row);
        setsList.addView(card);
    }

    private void updateSelectorUI() {
        int selectedBg = ContextCompat.getColor(this, R.color.accent_volt);
        int unselectedBg = ContextCompat.getColor(this, R.color.divider);
        int selectedText = ContextCompat.getColor(this, R.color.on_accent_volt);
        int unselectedText = ContextCompat.getColor(this, R.color.text_primary);
        btnPushups.setBackgroundTintList(ColorStateList.valueOf(isPushups ? selectedBg : unselectedBg));
        btnPullups.setBackgroundTintList(ColorStateList.valueOf(isPushups ? unselectedBg : selectedBg));
        btnPushups.setTextColor(isPushups ? selectedText : unselectedText);
        btnPullups.setTextColor(isPushups ? unselectedText : selectedText);
    }

    private void updateStepperUI() {
        tvRepCount.setText(String.valueOf(currentReps));
        btnLog.setText(getString(R.string.log_button_format, currentReps, currentExercise));
        btnMinus.setEnabled(currentReps > MIN_REPS);
    }

    private void updateTotals() {
        int totalReps = 0;
        for (int reps : loggedReps) {
            totalReps += reps;
        }
        int totalSets = loggedExercises.size();

        tvTodayRepsValue.setText(String.valueOf(totalReps));
        tvTodaySetsValue.setText(String.valueOf(totalSets));
        tvTodaysSetsTitle.setText(getString(R.string.todays_sets_format, totalSets));
        tvDailyGoal.setText(getString(R.string.daily_goal_format, totalReps));
        progressDaily.setMax(DAILY_GOAL);
        progressDaily.setProgress(Math.min(DAILY_GOAL, totalReps));

        tvPrPushValue.setText(bestPushups > 0
                ? getString(R.string.pr_value_format, bestPushups)
                : getString(R.string.pr_empty));
        tvPrPullValue.setText(bestPullups > 0
                ? getString(R.string.pr_value_format, bestPullups)
                : getString(R.string.pr_empty));

        int remaining = DAILY_GOAL - totalReps;
        if (remaining > 0) {
            tvMotivation.setText(getString(R.string.motivation_remaining, remaining));
        } else {
            tvMotivation.setText(getString(R.string.motivation_done));
        }

        ivEmpty.setVisibility(totalSets == 0 ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}

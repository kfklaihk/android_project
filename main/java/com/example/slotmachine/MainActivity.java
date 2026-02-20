package com.example.slotmachine;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Arrays;
import java.util.Random;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    private TextView digit1, digit2, digit3, resultText;
    private Button reel1Button, reel2Button, reel3Button, newGameButton;
    private Handler handler = new Handler();
    private boolean isReel1Running, isReel2Running, isReel3Running;
    private int currentDigit1, currentDigit2, currentDigit3;
    private int buttonsPressed = 0;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        digit1 = findViewById(R.id.digit1);
        digit2 = findViewById(R.id.digit2);
        digit3 = findViewById(R.id.digit3);
        resultText = findViewById(R.id.resultText);
        reel1Button = findViewById(R.id.reel1Button);
        reel2Button = findViewById(R.id.reel2Button);
        reel3Button = findViewById(R.id.reel3Button);
        newGameButton = findViewById(R.id.newGameButton);

        setupReelButton(reel1Button, digit1, 1);
        setupReelButton(reel2Button, digit2, 2);
        setupReelButton(reel3Button, digit3, 3);

        // Disable reel 2 and 3 initially
        reel2Button.setEnabled(false);
        reel3Button.setEnabled(false);

        newGameButton.setOnClickListener(v -> resetGame());
    }

    private void setupReelButton(Button button, TextView digitView, int reelNumber) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startReel(digitView, reelNumber);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                stopReelAfterDelay(button, digitView, reelNumber);
                buttonsPressed++;
                
                // Enable next button if available
                if (reelNumber == 1) {
                    reel2Button.setEnabled(true);
                } else if (reelNumber == 2) {
                    reel3Button.setEnabled(true);
                }
                return true;
            }
            return false;
        });
    }

    private void startReel(TextView digitView, int reelNumber) {
        switch (reelNumber) {
            case 1: isReel1Running = true; break;
            case 2: isReel2Running = true; break;
            case 3: isReel3Running = true; break;
        }

        handler.post(new Runnable() {
            int number = 0;
            @Override
            public void run() {
                boolean isRunning = false;
                switch (reelNumber) {
                    case 1: isRunning = isReel1Running; break;
                    case 2: isRunning = isReel2Running; break;
                    case 3: isRunning = isReel3Running; break;
                }

                if (isRunning) {
                    digitView.setText(String.valueOf(number));
                    number = (number + 1) % 10;
                    handler.postDelayed(this, 333); // 3 numbers per second
                }
            }
        });
    }

    private void stopReelAfterDelay(Button button, TextView digitView, int reelNumber) {
        handler.postDelayed(() -> {
            switch (reelNumber) {
                case 1:
                    isReel1Running = false;
                    currentDigit1 = Integer.parseInt(digitView.getText().toString());
                    break;
                case 2:
                    isReel2Running = false;
                    currentDigit2 = Integer.parseInt(digitView.getText().toString());
                    break;
                case 3:
                    isReel3Running = false;
                    currentDigit3 = Integer.parseInt(digitView.getText().toString());
                    break;
            }
            button.setEnabled(false);
            checkWinningCombination();
        }, 2000); // Continue spinning for 2 more seconds
    }

    private void showResult(String message, String color) {
        resultText.setText(message);
        resultText.setTextColor(Color.parseColor(color));
    }

    private void checkWinningCombination() {
        if (buttonsPressed == 3) {
            if (currentDigit1 == currentDigit2 && currentDigit2 == currentDigit3) {
                showResult("Bingo!", "#FF4081");
            } else if (isConsecutive(currentDigit1, currentDigit2, currentDigit3)) {
                showResult("Excellent!", "#4CAF50");
            }
        }
    }

    private boolean isConsecutive(int a, int b, int c) {
        // Convert to array for easier handling
        int[] nums = {a, b, c};
        
        // Check ascending order (including round-robin)
        if ((nums[1] == (nums[0] + 1) % 10 && nums[2] == (nums[1] + 1) % 10) ||  // normal case: 1,2,3 or round-robin case: 8,9,0
            (nums[1] == (nums[0] + 1) && nums[2] == (nums[1] + 1)) ||            // normal case: 1,2,3
            (nums[0] == 9 && nums[1] == 0 && nums[2] == 1) ||                    // round-robin case: 9,0,1
            (nums[0] == 8 && nums[1] == 9 && nums[2] == 0)) {                    // round-robin case: 8,9,0
            return true;
        }
        
        // Check descending order (including round-robin)
        if ((nums[1] == (nums[0] - 1 + 10) % 10 && nums[2] == (nums[1] - 1 + 10) % 10) ||  // normal case: 3,2,1 or round-robin case: 1,0,9
            (nums[1] == (nums[0] - 1) && nums[2] == (nums[1] - 1)) ||                       // normal case: 3,2,1
            (nums[0] == 0 && nums[1] == 9 && nums[2] == 8) ||                               // round-robin case: 0,9,8
            (nums[0] == 1 && nums[1] == 0 && nums[2] == 9)) {                               // round-robin case: 1,0,9
            return true;
        }
        
        return false;
    }

    private void resetGame() {
        isReel1Running = isReel2Running = isReel3Running = false;
        reel1Button.setEnabled(true);
        reel2Button.setEnabled(false);
        reel3Button.setEnabled(false);
        digit1.setText("0");
        digit2.setText("0");
        digit3.setText("0");
        currentDigit1 = currentDigit2 = currentDigit3 = 0;
        buttonsPressed = 0;
        resultText.setText("");
    }
} 
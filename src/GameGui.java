import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Random;

public class GameGui {
    private final JFrame frame = new JFrame();
    private JPanel gamePanel, buttonsPanel, nextOperatorsPanel;
    private JLabel targetValueLabel, currentSumLabel, movesLeftLabel;
    private JLabel[] operatorsLabels;
    private final Border defaultBorder = BorderFactory.createEmptyBorder(5, 10, 5, 10);
    private int selectedButtonRow = -1, selectedButtonCol = -1;
    private int movesLeft;
    private Settings settings;
    private GridButton[][] buttons;
    private JSpinner rowSpinner, colSpinner, targetSpinner, moveSpinner;

    GameGui() {
        mainMenu();
    }

    public void mainMenu() {
        mainMenu(new Settings());
    }

    public void mainMenu(Settings settings) {
        this.settings = settings;

        // frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("More or less, less is more!");
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel mainMenuPanel = new JPanel(new BorderLayout());
        JPanel settingsPanel = new JPanel(new GridLayout(2, 1));
        JPanel spinnersPanel = new JPanel(new GridLayout(2, 2));
        JPanel[] labelAndSpinnerPanels = new JPanel[4];
        for (int i = 0; i < 4; i++) {
            labelAndSpinnerPanels[i] = new JPanel(new GridLayout(1, 2));
        }
        JPanel difficultyPresetsPanel = new JPanel(new GridLayout(1, 4));

        // spinners
        /*
        sources:
        https://docs.oracle.com/javase/7/docs/api/javax/swing/JSpinner.html
        https://docs.oracle.com/javase/7/docs/api/javax/swing/SpinnerNumberModel.html
        */

        // rows
        JLabel rowsLabel = new JLabel("Rows: ");
        rowsLabel.setHorizontalAlignment(JLabel.RIGHT);
        SpinnerModel value = new SpinnerNumberModel(
                settings.getNumOfRows(), // default value
                2, // minimum number of rows
                10, // maximum number of rows
                1 // spinner step
        );
        rowSpinner = new JSpinner(value);
        labelAndSpinnerPanels[0].add(rowsLabel);
        labelAndSpinnerPanels[0].add(rowSpinner);

        // columns
        JLabel colsLabel = new JLabel("Columns: ");
        colsLabel.setHorizontalAlignment(JLabel.RIGHT);
        value = new SpinnerNumberModel(
                settings.getNumOfCols(), // default value
                2, // minimum number of columns
                10, // maximum number of columns
                1 // spinner step
        );
        colSpinner = new JSpinner(value);
        labelAndSpinnerPanels[1].add(colsLabel);
        labelAndSpinnerPanels[1].add(colSpinner);

        // Target value
        JLabel targetValueLabel = new JLabel("Target value: ");
        targetValueLabel.setHorizontalAlignment(JLabel.RIGHT);
        value = new SpinnerNumberModel(
                settings.getTargetVal(), // default value
                1, // minimum target value
                300, // maximum target value
                1 // spinner step
        );
        targetSpinner = new JSpinner(value);
        labelAndSpinnerPanels[2].add(targetValueLabel);
        labelAndSpinnerPanels[2].add(targetSpinner);

        // Available moves
        JLabel movesValueLabel = new JLabel("Available moves: ");
        movesValueLabel.setHorizontalAlignment(JLabel.RIGHT);
        value = new SpinnerNumberModel(
                settings.getMovesLeft(), // default value
                1, // minimum moves
                99, // maximum moves
                1 // spinner step
        );
        moveSpinner = new JSpinner(value);
        labelAndSpinnerPanels[3].add(movesValueLabel);
        labelAndSpinnerPanels[3].add(moveSpinner);
        // that's it for spinners


        // start game buttons
        JPanel startGamePanel = new JPanel(new GridLayout(1, 2));
        JButton startNewGameButton = new JButton("Start new game!");
        startNewGameButton.addActionListener(e -> {
            settings.setNumOfRows((int) rowSpinner.getValue());
            settings.setNumOfCols((int) colSpinner.getValue());
            settings.setTargetValue((int) targetSpinner.getValue());
            settings.setMovesLeft((int) moveSpinner.getValue());
            frame.remove(mainMenuPanel);
            init();
        });
        JButton loadFileButton = new JButton("Load from file...");
        loadFileButton.addActionListener(e -> {
            // TODO: add function for loading from file
        });
        loadFileButton.setEnabled(false);

        JButton easyPresetButton = new JButton("Easy");
        easyPresetButton.addActionListener(e -> setSpinnersTo(3, 3, 50, 20));

        JButton mediumPresetButton = new JButton("Medium");
        mediumPresetButton.addActionListener(e -> setSpinnersTo(5, 5, 150, 15));

        JButton hardPresetButton = new JButton("Hard");
        hardPresetButton.addActionListener(e -> setSpinnersTo(7, 7, 300, 10));

        JLabel presetsLabel = new JLabel("Presets:");
        presetsLabel.setHorizontalAlignment(JLabel.CENTER);
        difficultyPresetsPanel.add(presetsLabel);
        difficultyPresetsPanel.add(easyPresetButton);
        difficultyPresetsPanel.add(mediumPresetButton);
        difficultyPresetsPanel.add(hardPresetButton);

        for (int i = 0; i < 4; i++) {
            labelAndSpinnerPanels[i].setBorder(
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
            );
            spinnersPanel.add(labelAndSpinnerPanels[i]);
        }

        settingsPanel.add(spinnersPanel);
        difficultyPresetsPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 2, 10, 2)
        );
        settingsPanel.add(difficultyPresetsPanel);

        startGamePanel.add(startNewGameButton);
        startGamePanel.add(loadFileButton);

        mainMenuPanel.add(settingsPanel, BorderLayout.CENTER);
        mainMenuPanel.add(startGamePanel, BorderLayout.SOUTH);
        frame.add(mainMenuPanel);
        frame.pack();
        frame.setVisible(true);
        frame.revalidate();
    }

    public void init(){
        selectedButtonRow = -1;
        selectedButtonCol = -1;
        movesLeft = settings.getMovesLeft();
        frame.setResizable(true);

        gamePanel = new JPanel(new BorderLayout());

        // panel
        buttonsPanel = new JPanel();
        buttonsPanel.setBorder(defaultBorder);
        buttonsPanel.setLayout(new GridLayout(settings.getNumOfRows(), settings.getNumOfCols()));


        // top panel
        JPanel topPanel = new JPanel(new GridLayout(0, 3));
        topPanel.setBorder(defaultBorder);

        changeButtonGridSize(settings.getNumOfRows(), settings.getNumOfCols());

        // next operators panel
        nextOperatorsPanel = new JPanel(new GridLayout(settings.getNumOfRows(), 1));
        nextOperatorsPanel.setBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 15)
        );
        setupNextOperators();

        JButton saveButton = new JButton("Save & Quit");
        saveButton.addActionListener(e -> {
            // TODO: create function for saving
            System.out.println("saved");
            System.out.println("i swear");
        });
        saveButton.setEnabled(false);
        topPanel.add(saveButton);

        targetValueLabel = new JLabel("Target value:");
        targetValueLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(targetValueLabel, BorderLayout.CENTER);

        currentSumLabel = new JLabel("Current sum:");
        currentSumLabel.setVerticalAlignment(JLabel.CENTER);
        currentSumLabel.setBorder(defaultBorder);
        currentSumLabel.setHorizontalAlignment(JLabel.CENTER);
        gamePanel.add(currentSumLabel, BorderLayout.SOUTH);

        movesLeftLabel = new JLabel("Moves left:");
        movesLeftLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(movesLeftLabel, BorderLayout.EAST);


        gamePanel.add(topPanel, BorderLayout.NORTH);
        gamePanel.add(buttonsPanel, BorderLayout.CENTER);
        gamePanel.add(nextOperatorsPanel, BorderLayout.EAST);
        frame.add(gamePanel);
        frame.pack();
        frame.setVisible(true);

        printTargetValue();
        updateCurrentSum(settings.getNumOfRows(), settings.getNumOfCols());
        moveDone(true);
        frame.revalidate();
    }

    public void changeButtonGridSize(int x, int y) {
        buttons = new GridButton[x][y];
        addButtons(x, y);
    }

    public void setSpinnersTo(int rows, int cols, int target, int moves) {
        rowSpinner.setValue(rows);
        colSpinner.setValue(cols);
        targetSpinner.setValue(target);
        moveSpinner.setValue(moves);
    }

    public void addButtons(int x, int y) {
        Random rand = new Random();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                buttons[i][j] = new GridButton(rand.nextInt(9), i, j);
                buttonsPanel.add(buttons[i][j]);
            }
        }
        addButtonActionListeners(x, y);
    }
    
    public void setupNextOperators() {
        operatorsLabels = new JLabel[settings.getNumOfRows()];
        for (int i = 0; i < settings.getNumOfRows(); i++) {
            operatorsLabels[i] = new JLabel(getRandOperator());
            if(i < movesLeft) {
                if (i == 0) {
                    operatorsLabels[i].setFont(new Font("Arial", Font.BOLD, 22));
                    operatorsLabels[i].setHorizontalAlignment(JLabel.LEFT);
                }
                else {
                    operatorsLabels[i].setHorizontalAlignment(JLabel.CENTER);
                }
                nextOperatorsPanel.add(operatorsLabels[i]);
            }

        }
    }

    public void updateOperators() {
        for (int i = 0; i < operatorsLabels.length - 1; i++) {
            operatorsLabels[i].setText(operatorsLabels[i + 1].getText());
        }
        operatorsLabels[operatorsLabels.length - 1].setText(getRandOperator());
    }

    public String getRandOperator() {
        Random random = new Random();
        switch (random.nextInt(4)) {
            case 0 -> {
                return "+";
            }
            case 1 -> {
                return "-";
            }
            case 2 -> {
                return "*";
            }
            case 3 -> {
                return "/";
            }
            default -> {
                System.out.println("Error: random operator!");
                return "error";
            }
        }
    }

    public void printTargetValue() {
        this.targetValueLabel.setText("Target value: " + settings.getTargetVal());
    }

    public int updateCurrentSum(int rows, int cols) {
        int sum = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum = sum + buttons[i][j].getValue();
            }
        }
        currentSumLabel.setText("Current sum: " + sum);
        return sum;
    }

    public void addButtonActionListeners(int maxRow, int maxCol) {
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                addButtonActionListener(i, j, maxRow, maxCol);
            }
        }
    }

    public void addButtonActionListener(int currentX, int currentY, int maxRow, int maxCol) {
        buttons[currentX][currentY].addActionListener(e -> {
            if (selectedButtonRow == -1 && selectedButtonCol == -1) { // when first click
                selectedButtonRow = buttons[currentX][currentY].getRow();
                selectedButtonCol = buttons[currentX][currentY].getCol();
                createAvailableButtonsCross(maxRow, maxCol);
            }
            else { // all other buttons clicked after first one
                int result = 0;
                int arg1 = buttons[selectedButtonRow][selectedButtonCol].getValue();
                int arg2 = buttons[currentX][currentY].getValue();
                switch (operatorsLabels[0].getText()) {
                    case "+" -> result = arg1 + arg2;
                    case "-" -> result = arg1 - arg2;
                    case "*" -> result = arg1 * arg2;
                    case "/" -> result = arg1 / arg2;
                }
                result = Math.abs(result % 10);
                buttons[selectedButtonRow][selectedButtonCol].setValue(result);
                if (selectedButtonRow != -1) {
                    updateOperators();
                }
                selectedButtonRow = buttons[currentX][currentY].getRow();
                selectedButtonCol = buttons[currentX][currentY].getCol();
                createAvailableButtonsCross(maxRow, maxCol);
                updateCurrentSum(maxRow, maxCol);
                moveDone(); // subtracts 1 from movesLeft and updates label
            }
        });
    }

    public void createAvailableButtonsCross(int maxRow, int maxCol) {
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                if ((selectedButtonRow == i && selectedButtonCol == j) ||
                    (operatorsLabels[0].getText().equals("/") && buttons[i][j].getText().equals("0"))
                ) {
                    buttons[i][j].setEnabled(false);
                } else {
                    buttons[i][j].setEnabled(selectedButtonRow == i || selectedButtonCol == j);
                }
            }
        }
    }

    public void moveDone() {
        moveDone(false);
    }

    public void moveDone(boolean init) {
        if (!init) {
            movesLeft--;
            int points = updateCurrentSum(settings.getNumOfRows(), settings.getNumOfCols());
            if (movesLeft <= 0 && points != settings.getTargetVal()) {
                String pointsStr = "You were " + Math.abs(settings.getTargetVal() - points);
                postGame("YOU LOST! " + pointsStr + " point/s away from target number.");
            } else if (updateCurrentSum(settings.getNumOfRows(), settings.getNumOfCols()) == settings.getTargetVal()) {
                postGame("YOU WIN!");
            }
        }
        movesLeftLabel.setText("Moves left: " + movesLeft);
        frame.revalidate();
    }
    public void postGame(String labelText) {
        frame.remove(gamePanel);
        JPanel postGamePanel = new JPanel(new BorderLayout());
        JPanel whatsNextButtons = new JPanel(new GridLayout(1, 2));

        JLabel postGameLabel = new JLabel(labelText);
        postGameLabel.setHorizontalAlignment(JLabel.CENTER);
        postGameLabel.setBorder(
                BorderFactory.createEmptyBorder(30, 20, 30, 20)
        );
        postGamePanel.add(postGameLabel);

        JButton changeSettingsButton = new JButton("Main menu...");
        changeSettingsButton.addActionListener(e -> {
            frame.remove(postGamePanel);
            mainMenu(settings);
        });
        JButton playAgainButton = new JButton("Play again!");
        playAgainButton.addActionListener(e -> {
            frame.remove(postGamePanel);
            init();
        });
        whatsNextButtons.add(playAgainButton);
        whatsNextButtons.add(changeSettingsButton);
        postGamePanel.add(whatsNextButtons, BorderLayout.SOUTH);
        frame.add(postGamePanel);
        frame.pack();
    }
}

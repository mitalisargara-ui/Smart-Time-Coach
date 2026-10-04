import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.SwingWorker;

import javax.swing.border.EmptyBorder;

import java.awt.BasicStroke;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * ============================================================
 * SMART TIME COACH
 * ============================================================
 *
 * AI-Based Activity Timer & Goal Progress System
 *
 * Java Swing
 * SQLite
 * JDBC
 * OpenAI API
 *
 * ============================================================
 */
public class Main extends JFrame {

    // =========================================================
    // GOAL
    // =========================================================

    private int currentGoalId = -1;

    private JTextField goalNameField;
    private JTextField targetHoursField;
    private JTextField deadlineField;

    private JLabel goalLabel;
    private JLabel deadlineLabel;

    // =========================================================
    // DAILY TARGET
    // =========================================================

    private JTextField dailyTargetField;
    private JLabel dailyTargetLabel;

    // =========================================================
    // TIMER
    // =========================================================

    private JComboBox<String> activityCombo;
    private JTextField durationField;

    private JLabel timerLabel;

    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;

    private Timer swingTimer;

    private int remainingSeconds = 0;
    private int elapsedSeconds = 0;
    private int plannedSeconds = 0;

    private boolean timerRunning = false;

    private LocalDateTime timerStartTime;

    // Real timer deadline. Because this uses System.nanoTime(),
    // the timer keeps counting while this window is unfocused
    // or minimized.
    private long timerEndNanos = 0L;

    // =========================================================
    // PROGRESS
    // =========================================================

    private JProgressBar todayProgressBar;
    private JProgressBar overallProgressBar;
    private JProgressBar roadmapProgressBar;
    private JProgressBar smartProgressBar;

    private JLabel todayStudyLabel;
    private JLabel overallStudyLabel;
    private JLabel roadmapProgressLabel;
    private JLabel smartProgressLabel;

    // =========================================================
    // ROADMAP
    // =========================================================

    private DefaultListModel<String> roadmapModel;
    private JList<String> roadmapList;

    // =========================================================
    // 7 DAY
    // =========================================================

    private JLabel sevenDayTotalLabel;
    private JLabel sevenDayAverageLabel;
    private JLabel streakLabel;
    private JLabel consistencyLabel;
    private JLabel topActivityLabel;

    private JTextArea sevenDayArea;

    // =========================================================
    // HISTORY
    // =========================================================

    private JTextArea historyArea;

    // =========================================================
    // AI
    // =========================================================

    private JTextField aiQuestionField;
    private JTextArea aiOutputArea;
    private JButton askAIButton;

    // =========================================================
    // DASHBOARD
    // =========================================================

    private StudyChartPanel chartPanel;

    private JLabel dashboardGoal;
    private JLabel dashboardToday;
    private JLabel dashboardOverall;
    private JLabel dashboardRoadmap;
    private JLabel dashboardSmart;

    // =========================================================
    // BENCHMARK
    // =========================================================

    private JTextArea benchmarkArea;

    private JLabel benchmarkStatusLabel;
    private JProgressBar benchmarkProgressBar;

    // =========================================================
    // AI INSIGHTS / SMART PLANNER
    // =========================================================

    private JTextArea aiInsightsArea;
    private JTextField whatIfField;

    private JLabel goalHealthLabel;
    private JLabel executionLabel;
    private JLabel insightConsistencyLabel;
    private JLabel insightRoadmapLabel;

    private JProgressBar goalHealthBar;
    private JProgressBar executionBar;

    private JButton aiCreateGoalButton;

    // =========================================================
    // ADVANCED AI LAB FEATURES
    // =========================================================

    private JTextArea aiLabOutputArea;

    // =========================================================
    // AI RESOURCE CENTER
    // =========================================================

    private JTextArea resourceOutputArea;
    private JButton generateResourcesButton;
    private JLabel resourceGoalLabel;

    // =========================================================
    // MODERN NAVIGATION
    // =========================================================

    private CardLayout contentCardLayout;
    private JPanel contentCardPanel;
    private JButton activeNavButton;
    private JLabel headerGoalLabel;
    private JButton dashboardNavButton;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Main() {

        applyModernTheme();
        DatabaseManager.createTables();

        setTitle(
                "Smart Time Coach - AI Productivity System"
        );

        setSize(
                1400,
                900
        );

        setMinimumSize(
                new Dimension(1180, 760)
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        buildUI();

        loadSavedGoal();

        setVisible(true);
    }

    // =========================================================
    // BUILD UI
    // =========================================================

    private void applyModernTheme() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        UIManager.put("Panel.background", new Color(244, 247, 251));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 15));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 15));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 16));
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TabbedPane.background", new Color(244, 247, 251));
        UIManager.put("TabbedPane.foreground", new Color(25, 39, 58));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    // =========================================================
    // BUILD UI
    // =========================================================

    private void buildUI() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(243, 246, 250));

        // -----------------------------------------------------
        // TOP HEADER
        // -----------------------------------------------------
        JPanel header = new JPanel(new BorderLayout(18, 0));
        header.setBackground(new Color(11, 31, 54));
        header.setBorder(new EmptyBorder(14, 22, 14, 22));
        header.setPreferredSize(new Dimension(0, 78));

        JPanel brand = new JPanel(new BorderLayout(0, 2));
        brand.setOpaque(false);

        JLabel title = new JLabel(
                "SMART TIME COACH",
                createNavIcon("status"),
                SwingConstants.LEFT
        );
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setIconTextGap(10);

        JLabel subtitle = new JLabel(
                "AI GOAL MANAGEMENT  •  SMART PLANNING  •  FOCUSED EXECUTION",
                SwingConstants.LEFT
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(new Color(164, 190, 215));

        brand.add(title, BorderLayout.CENTER);
        brand.add(subtitle, BorderLayout.SOUTH);
        header.add(brand, BorderLayout.WEST);

        JPanel headerRight = new JPanel(new BorderLayout(6, 4));
        headerRight.setOpaque(false);

        JLabel workspace = new JLabel(
                "SMART WORKSPACE",
                createNavIcon("status"),
                SwingConstants.RIGHT
        );
        workspace.setFont(new Font("Segoe UI", Font.BOLD, 11));
        workspace.setForeground(new Color(112, 229, 177));
        workspace.setHorizontalTextPosition(SwingConstants.LEFT);
        workspace.setIconTextGap(7);

        headerGoalLabel = new JLabel(
                "Goal: —",
                SwingConstants.RIGHT
        );
        headerGoalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        headerGoalLabel.setForeground(new Color(231, 239, 247));

        headerRight.add(workspace, BorderLayout.NORTH);
        headerRight.add(headerGoalLabel, BorderLayout.SOUTH);
        header.add(headerRight, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        // -----------------------------------------------------
        // LEFT SIDEBAR + CONTENT
        // -----------------------------------------------------
        JPanel workspacePanel = new JPanel(new BorderLayout());
        workspacePanel.setBackground(new Color(243, 246, 250));

        JPanel sidebar = createModernSidebar();
        workspacePanel.add(sidebar, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentCardPanel = new JPanel(contentCardLayout);
        contentCardPanel.setBackground(new Color(243, 246, 250));
        contentCardPanel.setBorder(new EmptyBorder(14, 14, 10, 14));

        addContentCard("DASHBOARD", createDashboardPanel());
        addContentCard("GOALS", createGoalPanel());
        addContentCard("AI COACH", createAIPanel());
        addContentCard("AI LAB", createAIInsightsPanel());
        addContentCard("AI RESOURCES", createAIResourceCenterPanel());
        addContentCard("ROADMAP", createRoadmapPanel());
        addContentCard("TIMER", createTimerPanel());
        addContentCard("PROGRESS", createProgressPanel());
        addContentCard("7-DAY ANALYTICS", createSevenDayPanel());
        addContentCard("ACTIVITY HISTORY", createHistoryPanel());
        addContentCard("BENCHMARK", createBenchmarkPanel());

        workspacePanel.add(contentCardPanel, BorderLayout.CENTER);
        root.add(workspacePanel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(232, 237, 243));
        footer.setBorder(new EmptyBorder(7, 18, 7, 18));
        JLabel footerLabel = new JLabel(
                "YOUR DATA • YOUR PLAN • YOUR EXECUTION • YOUR PROGRESS",
                SwingConstants.CENTER
        );
        footerLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        footerLabel.setForeground(new Color(91, 105, 120));
        footer.add(footerLabel, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        add(root);

        // Dashboard opens first.
        selectSection("DASHBOARD", dashboardNavButton);
    }

    private JPanel createModernSidebar() {

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(228, 0));
        sidebar.setBackground(new Color(18, 39, 64));
        sidebar.setBorder(new EmptyBorder(14, 12, 14, 12));

        JPanel top = new JPanel(new BorderLayout(0, 5));
        top.setOpaque(false);

        JLabel navTitle = new JLabel("WORKSPACE", SwingConstants.LEFT);
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        navTitle.setForeground(Color.WHITE);

        JLabel navHint = new JLabel("Navigate your goal journey", SwingConstants.LEFT);
        navHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        navHint.setForeground(new Color(157, 180, 202));

        top.add(navTitle, BorderLayout.NORTH);
        top.add(navHint, BorderLayout.SOUTH);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel navList = new JPanel(new GridLayout(11, 1, 0, 6));
        navList.setOpaque(false);
        navList.setBorder(new EmptyBorder(18, 0, 12, 0));

        dashboardNavButton = addNavButton(navList, "DASHBOARD", "home");
        addNavButton(navList, "GOALS", "goal");
        addNavButton(navList, "AI COACH", "ai");
        addNavButton(navList, "AI LAB", "insights");
        addNavButton(navList, "AI RESOURCES", "resources");
        addNavButton(navList, "ROADMAP", "roadmap");
        addNavButton(navList, "TIMER", "timer");
        addNavButton(navList, "PROGRESS", "progress");
        addNavButton(navList, "7-DAY ANALYTICS", "calendar");
        addNavButton(navList, "ACTIVITY HISTORY", "history");
        addNavButton(navList, "BENCHMARK", "benchmark");

        sidebar.add(navList, BorderLayout.CENTER);

        JPanel sideBottom = new JPanel(new BorderLayout(0, 3));
        sideBottom.setOpaque(false);

        JLabel aiLabel = new JLabel(
                "AI-POWERED",
                createNavIcon("ai"),
                SwingConstants.LEFT
        );
        aiLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        aiLabel.setForeground(new Color(104, 214, 255));
        aiLabel.setIconTextGap(7);

        JLabel sideNote = new JLabel(
                "Set a goal → plan → execute → adapt",
                SwingConstants.LEFT
        );
        sideNote.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        sideNote.setForeground(new Color(145, 167, 188));

        sideBottom.add(aiLabel, BorderLayout.NORTH);
        sideBottom.add(sideNote, BorderLayout.SOUTH);
        sidebar.add(sideBottom, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addContentCard(String name, JPanel panel) {
        contentCardPanel.add(panel, name);
    }

    private JButton addNavButton(JPanel parent, String name, String iconType) {

        JButton button = new JButton(name);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(new Color(215, 226, 237));
        button.setBackground(new Color(18, 39, 64));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIcon(createSidebarIcon(iconType, new Color(159, 187, 211)));
        button.setIconTextGap(12);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 12, 10, 10));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addActionListener(e -> selectSection(name, button));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(new Color(28, 57, 87));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(new Color(18, 39, 64));
                }
            }
        });

        parent.add(button);
        return button;
    }

    private void selectSection(String name, JButton source) {

        if (contentCardLayout == null || contentCardPanel == null) {
            return;
        }

        contentCardLayout.show(contentCardPanel, name);

        if (activeNavButton != null) {
            activeNavButton.setBackground(new Color(18, 39, 64));
            activeNavButton.setForeground(new Color(215, 226, 237));
        }

        if (source != null) {
            activeNavButton = source;
            activeNavButton.setBackground(new Color(37, 99, 235));
            activeNavButton.setForeground(Color.WHITE);
        }

        if ("DASHBOARD".equals(name)) {
            updateAllProgress();
        }
    }

    private Icon createSidebarIcon(String type, Color color) {
        return new SimpleUiIcon(type, color, 18, 18);
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    private JPanel createDashboardPanel() {

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(10, 6, 8, 6));

        JPanel cards = new JPanel(new GridLayout(1, 5, 10, 10));
        cards.setOpaque(false);

        dashboardGoal = dashboardCard("GOAL", "NONE");
        dashboardToday = dashboardCard("TODAY", "0%");
        dashboardOverall = dashboardCard("OVERALL", "0%");
        dashboardRoadmap = dashboardCard("ROADMAP", "0%");
        dashboardSmart = dashboardCard("SMART", "0%");

        cards.add(dashboardGoal);
        cards.add(dashboardToday);
        cards.add(dashboardOverall);
        cards.add(dashboardRoadmap);
        cards.add(dashboardSmart);
        panel.add(cards, BorderLayout.NORTH);

        JPanel chartCard = new JPanel(new BorderLayout(8, 8));
        chartCard.setBackground(Color.WHITE);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 231), 1),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel chartTitle = new JLabel("7-DAY STUDY ANALYTICS", createNavIcon("progress"), SwingConstants.LEFT);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        chartTitle.setForeground(new Color(18, 52, 86));
        chartCard.add(chartTitle, BorderLayout.NORTH);

        chartPanel = new StudyChartPanel();
        chartCard.add(chartPanel, BorderLayout.CENTER);
        panel.add(chartCard, BorderLayout.CENTER);

        JButton refresh = createBigButton("↻  REFRESH DASHBOARD");
        refresh.setPreferredSize(new Dimension(250, 42));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 7));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 58));
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);
        refresh.addActionListener(e -> updateAllProgress());

        return panel;
    }


    // =========================================================
    // DASHBOARD CARD
    // =========================================================

    private JLabel dashboardCard(String title, String value) {

        JLabel label = new JLabel(
                "<html><div style='text-align:center;'>"
                + "<span style='font-size:11px;color:#64748B;'>" + title + "</span><br>"
                + "<span style='font-size:22px;color:#0F2742;'><b>" + value + "</b></span>"
                + "</div></html>",
                SwingConstants.CENTER);

        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(15, 39, 66));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setPreferredSize(new Dimension(210, 78));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(214, 223, 232), 1),
                new EmptyBorder(10, 8, 10, 8)));
        return label;
    }


    // =========================================================
    // GOAL PANEL
    // =========================================================

    private JPanel createGoalPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(16, 16));
        mainPanel.setBorder(new EmptyBorder(18, 28, 18, 28));
        mainPanel.setBackground(new Color(245, 248, 252));

        // -----------------------------------------------------
        // TOP HEADING
        // -----------------------------------------------------
        JPanel topPanel = new JPanel(new BorderLayout(6, 4));
        topPanel.setOpaque(false);

        JLabel heading = new JLabel("AI GOAL SETUP", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(new Color(25, 55, 90));
        heading.setIcon(createNavIcon("goal"));
        heading.setIconTextGap(10);
        topPanel.add(heading, BorderLayout.CENTER);

        JLabel subHeading = new JLabel(
                "Enter your goal below. AI will prepare the roadmap and requirements.",
                SwingConstants.CENTER);
        subHeading.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subHeading.setForeground(new Color(80, 95, 110));
        topPanel.add(subHeading, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // -----------------------------------------------------
        // CONTENT CARD
        // -----------------------------------------------------
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 150, 180), 1),
                new EmptyBorder(24, 28, 24, 28)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // -----------------------------------------------------
        // GOAL INPUT - ALWAYS CLEAR AND EASY TO USE
        // -----------------------------------------------------
        JLabel goalSection = new JLabel("ENTER YOUR GOAL");
        goalSection.setFont(new Font("Segoe UI", Font.BOLD, 19));
        goalSection.setForeground(new Color(25, 55, 90));
        goalSection.setIcon(createNavIcon("goal"));
        goalSection.setIconTextGap(8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(goalSection, gbc);

        JLabel goalNameLabel = createBigLabel("GOAL NAME");
        goalNameField = createBigField();
        goalNameField.setToolTipText(
                "Examples: IIT Preparation, UPSC Preparation, NEET Preparation, Coding");
        goalNameField.setPreferredSize(new Dimension(520, 48));
        goalNameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.25;
        card.add(goalNameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        card.add(goalNameField, gbc);

        // -----------------------------------------------------
        // AI CREATE BUTTON
        // -----------------------------------------------------
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(18, 12, 8, 12);

        aiCreateGoalButton = createBigButton("CREATE GOAL WITH AI");
        aiCreateGoalButton.setPreferredSize(new Dimension(420, 56));
        aiCreateGoalButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(aiCreateGoalButton, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(4, 12, 18, 12);
        JLabel aiInfo = new JLabel(
                "Only the goal is required. AI will generate the roadmap, requirements and progress criteria.",
                SwingConstants.CENTER);
        aiInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aiInfo.setForeground(new Color(80, 95, 110));
        card.add(aiInfo, gbc);

        // -----------------------------------------------------
        // OPTIONAL MANUAL SETTINGS
        // -----------------------------------------------------
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 12, 8, 12);
        JLabel manualTitle = new JLabel("OPTIONAL MANUAL SETTINGS", SwingConstants.CENTER);
        manualTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        manualTitle.setForeground(new Color(100, 110, 125));
        card.add(manualTitle, gbc);

        JLabel targetLabel = createBigLabel("OVERALL TARGET HOURS");
        JLabel deadlineText = createBigLabel("DEADLINE (DAYS)");
        JLabel dailyTargetText = createBigLabel("TODAY'S TARGET (MINUTES)");

        targetHoursField = createBigField();
        deadlineField = createBigField();
        dailyTargetField = createBigField();

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 12, 10, 12);

        gbc.gridy = 5;
        gbc.gridx = 0;
        card.add(targetLabel, gbc);
        gbc.gridx = 1;
        card.add(targetHoursField, gbc);

        gbc.gridy = 6;
        gbc.gridx = 0;
        card.add(deadlineText, gbc);
        gbc.gridx = 1;
        card.add(deadlineField, gbc);

        gbc.gridy = 7;
        gbc.gridx = 0;
        JButton saveGoal = createBigButton("SAVE GOAL");
        card.add(saveGoal, gbc);

        gbc.gridx = 1;
        goalLabel = createInfoLabel("CURRENT GOAL: NONE");
        card.add(goalLabel, gbc);

        gbc.gridy = 8;
        gbc.gridx = 0;
        card.add(dailyTargetText, gbc);
        gbc.gridx = 1;
        card.add(dailyTargetField, gbc);

        gbc.gridy = 9;
        gbc.gridx = 0;
        JButton saveDaily = createBigButton("SAVE TODAY'S TARGET");
        card.add(saveDaily, gbc);

        gbc.gridx = 1;
        dailyTargetLabel = createInfoLabel("TODAY'S TARGET: 0 MIN");
        card.add(dailyTargetLabel, gbc);

        gbc.gridy = 10;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        deadlineLabel = new JLabel("REMAINING DAYS: -", SwingConstants.CENTER);
        deadlineLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        deadlineLabel.setForeground(new Color(180, 70, 40));
        card.add(deadlineLabel, gbc);

        // Put the card into a scroll pane so the goal input/buttons never disappear
        // on smaller NetBeans/Windows display sizes.
        JScrollPane scrollPane = new JScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(245, 248, 252));
        scrollPane.getViewport().setBackground(new Color(245, 248, 252));
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // -----------------------------------------------------
        // AI BUTTON ACTION
        // -----------------------------------------------------
        aiCreateGoalButton.addActionListener(e -> createGoalWithAI());

        saveGoal.addActionListener(e -> saveGoal());
        saveDaily.addActionListener(e -> saveDailyTarget());

        return mainPanel;
    }

    private JLabel createBigLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(new Color(31, 48, 68));
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(new Color(37, 99, 235));
        return label;
    }

    private JTextField createBigField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        field.setPreferredSize(new Dimension(360, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(188, 202, 218), 1),
                new EmptyBorder(7, 10, 7, 10)));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createBigButton(String text) {

        String iconKey = "default";
        String cleanText = text;

        if (text.contains("REFRESH")) iconKey = "refresh";
        else if (text.contains("CREATE GOAL")) iconKey = "ai";
        else if (text.contains("SAVE")) iconKey = "save";
        else if (text.contains("START")) iconKey = "start";
        else if (text.contains("PAUSE")) iconKey = "pause";
        else if (text.contains("STOP")) iconKey = "stop";
        else if (text.contains("ADD")) iconKey = "add";
        else if (text.contains("COMPLETED")) iconKey = "complete";
        else if (text.contains("PENDING")) iconKey = "pending";
        else if (text.contains("ASK AI")) iconKey = "ai";
        else if (text.contains("DAILY AI")) iconKey = "ai";
        else if (text.contains("WEAKNESS")) iconKey = "analyze";
        else if (text.contains("RESCHEDULE")) iconKey = "calendar";
        else if (text.contains("ADAPTIVE")) iconKey = "roadmap";
        else if (text.contains("WHY AM I")) iconKey = "ai";
        else if (text.contains("RECOVERY")) iconKey = "status";
        else if (text.contains("REFERENCE")) iconKey = "benchmark";
        else if (text.contains("GOAL TWIN")) iconKey = "goal";
        else if (text.contains("BOTTLENECK")) iconKey = "analyze";
        else if (text.contains("DEADLINE")) iconKey = "calendar";
        else if (text.contains("MINIMUM CHANGE")) iconKey = "analyze";
        else if (text.contains("WIND TUNNEL")) iconKey = "analyze";
        else if (text.contains("REVISION")) iconKey = "history";
        else if (text.contains("SKILL GAP")) iconKey = "roadmap";
        else if (text.contains("FOCUS QUALITY")) iconKey = "timer";
        else if (text.contains("ACHIEVEMENTS")) iconKey = "complete";
        else if (text.contains("RECOVERY")) iconKey = "status";
        else if (text.contains("SIMULATE")) iconKey = "analyze";
        else if (text.contains("ANALYZE")) iconKey = "analyze";

        cleanText = cleanButtonText(text);

        JButton button = new JButton(cleanText);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(210, 42));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(37, 99, 235));
        button.setIcon(createActionIcon(iconKey, Color.WHITE));
        button.setIconTextGap(9);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(22, 73, 116), 1),
                new EmptyBorder(6, 14, 6, 14)));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(59, 130, 246));
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(31, 92, 145));
                    button.setForeground(Color.WHITE);
                }
            }
        });

        return button;
    }

    private String cleanButtonText(String text) {
        String result = text;
        String[] prefixes = {
            "↻  ", "↻ ", "🤖  ", "💾  ", "▶  ", "⏸  ", "■  ",
            "＋ ", "☑ ", "☐ ", "✨  "
        };
        for (String prefix : prefixes) {
            if (result.startsWith(prefix)) {
                result = result.substring(prefix.length());
                break;
            }
        }
        return result.toUpperCase();
    }

    private Icon createActionIcon(String type, Color color) {
        return new SimpleUiIcon(type, color, 18, 18);
    }

    private Icon createNavIcon(String type) {
        return new SimpleUiIcon(type, new Color(31, 92, 145), 18, 18);
    }


    // =========================================================
    // TIMER PANEL
    // =========================================================

    private JPanel createTimerPanel() {

        JPanel outer = new JPanel(new BorderLayout(14, 14));
        outer.setBackground(new Color(245, 248, 252));
        outer.setBorder(new EmptyBorder(22, 55, 12, 55));

        JLabel heading = new JLabel(
                "⏱  ACTIVITY TIMER",
                SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 30));
        heading.setForeground(new Color(25, 55, 90));
        outer.add(heading, BorderLayout.NORTH);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(130, 160, 190), 2),
                new EmptyBorder(20, 30, 20, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel activityLabel = createBigLabel("ACTIVITY:");
        card.add(activityLabel, gbc);

        gbc.gridx = 1;
        activityCombo = new JComboBox<>(new String[]{
                "Study", "Reading", "Coding", "Work", "Exercise", "Research",
                "Project", "Meeting", "Other", "History", "Polity", "Geography",
                "Economy", "CSAT", "Current Affairs", "Revision", "PYQs", "Mock Test"
        });
        activityCombo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        activityCombo.setPreferredSize(new Dimension(300, 42));
        card.add(activityCombo, gbc);

        gbc.gridx = 0; gbc.gridy++;
        card.add(createBigLabel("DURATION (MINUTES):"), gbc);

        gbc.gridx = 1;
        durationField = createBigField();
        durationField.setPreferredSize(new Dimension(300, 42));
        card.add(durationField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 58));
        timerLabel.setForeground(new Color(15, 32, 55));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(244, 248, 252));
        timerLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        card.add(timerLabel, gbc);

        outer.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        buttons.setOpaque(false);
        buttons.setPreferredSize(new Dimension(0, 60));

        startButton = createBigButton("▶  START");
        pauseButton = createBigButton("⏸  PAUSE");
        stopButton = createBigButton("■  STOP");

        startButton.setPreferredSize(new Dimension(145, 44));
        pauseButton.setPreferredSize(new Dimension(145, 44));
        stopButton.setPreferredSize(new Dimension(145, 44));

        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);

        buttons.add(startButton);
        buttons.add(pauseButton);
        buttons.add(stopButton);

        outer.add(buttons, BorderLayout.SOUTH);

        startButton.addActionListener(e -> startTimer());
        pauseButton.addActionListener(e -> pauseTimer());
        stopButton.addActionListener(e -> stopTimer());

        return outer;
    }


    // =========================================================
    // PROGRESS PANEL
    // =========================================================

    private JPanel createProgressPanel() {

        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel heading = new JLabel("PROGRESS ANALYTICS", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(18, 52, 86));
        heading.setIcon(createNavIcon("calendar"));
        heading.setIconTextGap(10);
        panel.add(heading, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setOpaque(false);

        todayStudyLabel = createProgressLabel("TODAY'S STUDY: 00h 00m 00s");
        todayProgressBar = createProgressBar();
        cards.add(createProgressCard("TODAY'S PROGRESS", todayStudyLabel, todayProgressBar));

        overallStudyLabel = createProgressLabel("OVERALL STUDY: 00h 00m 00s");
        overallProgressBar = createProgressBar();
        cards.add(createProgressCard("OVERALL GOAL PROGRESS", overallStudyLabel, overallProgressBar));

        roadmapProgressLabel = createProgressLabel("ROADMAP PROGRESS: 0%");
        roadmapProgressBar = createProgressBar();
        cards.add(createProgressCard("ROADMAP COMPLETION", roadmapProgressLabel, roadmapProgressBar));

        smartProgressLabel = createProgressLabel("SMART GOAL PROGRESS: 0%");
        smartProgressBar = createProgressBar();
        cards.add(createProgressCard("SMART PROGRESS INDEX", smartProgressLabel, smartProgressBar));

        panel.add(cards, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createProgressLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(new Color(45, 58, 72));
        return label;
    }

    private JProgressBar createProgressBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bar.setPreferredSize(new Dimension(500, 32));
        bar.setForeground(new Color(37, 137, 99));
        bar.setBackground(new Color(226, 233, 240));
        return bar;
    }

    private JPanel createProgressCard(String title, JLabel label, JProgressBar bar) {
        JPanel card = new JPanel(new BorderLayout(10, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(16, 18, 16, 18)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(18, 52, 86));
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(label, BorderLayout.NORTH);
        center.add(bar, BorderLayout.CENTER);
        card.add(center, BorderLayout.CENTER);
        return card;
    }



    // =========================================================
    // ROADMAP PANEL
    // =========================================================

    private JPanel createRoadmapPanel() {

        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));

        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);
        JLabel heading = new JLabel("GOAL ROADMAP", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(18, 52, 86));
        header.add(heading, BorderLayout.CENTER);
        JLabel hint = new JLabel(
                "☐ Click any topic to mark it completed  •  ☑ Click again to reopen",
                SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(80, 98, 115));
        header.add(hint, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        roadmapModel = new DefaultListModel<>();
        roadmapList = new JList<>(roadmapModel);
        roadmapList.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        roadmapList.setFixedCellHeight(48);
        roadmapList.setBackground(Color.WHITE);
        roadmapList.setSelectionBackground(new Color(225, 238, 249));
        roadmapList.setSelectionForeground(new Color(18, 52, 86));
        roadmapList.setBorder(new EmptyBorder(6, 8, 6, 8));
        roadmapList.setCellRenderer(new RoadmapRenderer());

        roadmapList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = roadmapList.locationToIndex(e.getPoint());
                if (index < 0) return;
                java.awt.Rectangle bounds = roadmapList.getCellBounds(index, index);
                if (bounds != null && bounds.contains(e.getPoint())) {
                    String selected = roadmapModel.getElementAt(index);
                    String status = selected.endsWith("[Completed]") ? "Pending" : "Completed";
                    updateRoadmapByIndex(index, status);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(roadmapList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(196, 211, 224), 1));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.setOpaque(false);

        JButton addTopic = createBigButton("＋ ADD TOPIC");
        JButton complete = createBigButton("☑ MARK COMPLETED");
        JButton pending = createBigButton("☐ MARK PENDING");
        JButton addUPSC = createBigButton("＋ ADD UPSC ROADMAP");

        buttons.add(addTopic);
        buttons.add(complete);
        buttons.add(pending);
        buttons.add(addUPSC);
        panel.add(buttons, BorderLayout.SOUTH);

        addTopic.addActionListener(e -> addCustomTopic());
        complete.addActionListener(e -> updateSelectedRoadmap("Completed"));
        pending.addActionListener(e -> updateSelectedRoadmap("Pending"));

        addUPSC.addActionListener(e -> {
            if (currentGoalId == -1) {
                JOptionPane.showMessageDialog(this, "CREATE A GOAL FIRST.");
                return;
            }
            DatabaseManager.addDefaultUPSCRoadmap(currentGoalId);
            loadRoadmap();
            updateAllProgress();
            JOptionPane.showMessageDialog(this, "UPSC ROADMAP IS READY.");
        });

        return panel;
    }



    private class RoadmapRenderer extends JPanel implements ListCellRenderer<String> {

        private final JCheckBox checkBox = new JCheckBox();
        private final JLabel label = new JLabel();

        RoadmapRenderer() {
            setLayout(new BorderLayout(12, 0));
            setBorder(new EmptyBorder(5, 8, 5, 8));
            setOpaque(true);
            checkBox.setOpaque(false);
            checkBox.setFocusable(false);
            checkBox.setPreferredSize(new Dimension(34, 34));
            checkBox.setFont(new Font("Segoe UI", Font.BOLD, 18));
            label.setFont(new Font("Segoe UI", Font.BOLD, 17));
            add(checkBox, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list,
                String value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            String display = value;
            boolean completed = value.endsWith("[Completed]");

            int separator = value.indexOf("|");
            if (separator >= 0) {
                display = value.substring(separator + 1);
            }

            if (display.startsWith("✓ ")) display = display.substring(2);
            if (display.startsWith("□ ")) display = display.substring(2);

            int statusIndex = display.lastIndexOf(" [");
            if (statusIndex >= 0) display = display.substring(0, statusIndex);

            checkBox.setSelected(completed);
            label.setText(display);

            if (isSelected) {
                setBackground(new Color(220, 235, 250));
            } else {
                setBackground(Color.WHITE);
            }

            label.setForeground(completed
                    ? new Color(40, 130, 70)
                    : new Color(35, 45, 60));

            return this;
        }
    }


    // =========================================================
    // 7 DAY PANEL
    // =========================================================

    private JPanel createSevenDayPanel() {

        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));

        JLabel heading = new JLabel("7-DAY PERFORMANCE", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(18, 52, 86));
        heading.setIcon(createNavIcon("history"));
        heading.setIconTextGap(10);
        panel.add(heading, BorderLayout.NORTH);

        JPanel summary = new JPanel(new GridLayout(1, 5, 10, 10));
        summary.setOpaque(false);

        sevenDayTotalLabel = metricCard("TOTAL STUDY", "0h");
        sevenDayAverageLabel = metricCard("DAILY AVERAGE", "0h");
        streakLabel = metricCard("STREAK", "0 DAYS");
        consistencyLabel = metricCard("CONSISTENCY", "0%");
        topActivityLabel = metricCard("TOP ACTIVITY", "NONE");

        summary.add(sevenDayTotalLabel);
        summary.add(sevenDayAverageLabel);
        summary.add(streakLabel);
        summary.add(consistencyLabel);
        summary.add(topActivityLabel);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel reportTitle = new JLabel("PERFORMANCE REPORT", createNavIcon("progress"), SwingConstants.LEFT);
        reportTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        reportTitle.setForeground(new Color(18, 52, 86));
        center.add(reportTitle, BorderLayout.NORTH);

        sevenDayArea = new JTextArea();
        sevenDayArea.setEditable(false);
        sevenDayArea.setLineWrap(true);
        sevenDayArea.setWrapStyleWord(true);
        sevenDayArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        sevenDayArea.setMargin(new Insets(16, 16, 16, 16));
        sevenDayArea.setBackground(new Color(250, 252, 254));
        sevenDayArea.setText("NO 7-DAY DATA YET.\n\nCREATE A GOAL, SET A DAILY TARGET AND RECORD TIMER SESSIONS. YOUR WEEKLY ANALYTICS WILL APPEAR HERE.");

        JScrollPane scroll = new JScrollPane(sevenDayArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 226, 234), 1));
        center.add(scroll, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(12, 12));
        body.setOpaque(false);
        body.add(summary, BorderLayout.NORTH);
        body.add(center, BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);

        JButton refresh = createBigButton("↻  REFRESH PERFORMANCE");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 7));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 58));
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);
        refresh.addActionListener(e -> loadSevenDayPerformance());

        return panel;
    }

    private JLabel metricCard(String title, String value) {
        JLabel label = new JLabel(
                "<html><div style='text-align:center;'>"
                + title + "<br><b>" + value + "</b></div></html>",
                SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(18, 52, 86));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(10, 6, 10, 6)));
        return label;
    }



    // =========================================================
    // HISTORY PANEL
    // =========================================================

    private JPanel createHistoryPanel() {

        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));

        JLabel heading = new JLabel("ACTIVITY HISTORY", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(18, 52, 86));
        heading.setIcon(createNavIcon("ai"));
        heading.setIconTextGap(10);
        panel.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setBackground(Color.WHITE);
        center.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel info = new JLabel("SAVED TIMER SESSIONS FOR YOUR CURRENT GOAL", createNavIcon("history"), SwingConstants.LEFT);
        info.setFont(new Font("Segoe UI", Font.BOLD, 16));
        info.setForeground(new Color(18, 52, 86));
        center.add(info, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        historyArea.setMargin(new Insets(16, 16, 16, 16));
        historyArea.setBackground(new Color(250, 252, 254));
        historyArea.setText("NO ACTIVITY SESSIONS SAVED YET.\n\nSTART A TIMER SESSION AND PRESS STOP OR LET IT COMPLETE. YOUR SAVED ACTIVITY WILL APPEAR HERE.");

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(218, 226, 234), 1));
        center.add(scroll, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JButton refresh = createBigButton("↻  REFRESH HISTORY");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 7));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 58));
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);
        refresh.addActionListener(e -> loadHistory());

        return panel;
    }



    // =========================================================
    // AI PANEL
    // =========================================================

    private JPanel createAIPanel() {

        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));

        JLabel heading = new JLabel("SMART AI COACH", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 28));
        heading.setForeground(new Color(18, 52, 86));
        panel.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setMargin(new Insets(10, 12, 10, 12));
        info.setBackground(Color.WHITE);
        info.setText(
                "WHAT CAN YOU ASK?\n"
                + "• What should I study today?   • Am I on track?   • Analyze my last 7 days.\n"
                + "• Which activity needs attention?   • Analyze my roadmap.   • What should I do next?\n\n"
                + "SMART AI USES YOUR SAVED GOAL, DAILY TARGET, TIMER SESSIONS, ACTIVITY HISTORY, ROADMAP, "
                + "7-DAY PERFORMANCE AND YOUR INTERNAL GOAL-TIMELINE BENCHMARK.");

        JScrollPane infoScroll = new JScrollPane(info);
        infoScroll.setPreferredSize(new Dimension(1000, 145));
        infoScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                "HOW SMART AI COACH WORKS"));
        center.add(infoScroll, BorderLayout.NORTH);

        aiOutputArea = new JTextArea();
        aiOutputArea.setEditable(false);
        aiOutputArea.setLineWrap(true);
        aiOutputArea.setWrapStyleWord(true);
        aiOutputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        aiOutputArea.setMargin(new Insets(16, 16, 16, 16));
        aiOutputArea.setBackground(Color.WHITE);
        aiOutputArea.setText(
                "AI RESPONSE\n\n"
                + "Your personalized answer will appear here.\n\n"
                + "Ask about your goal, progress, roadmap, consistency or next study session.");

        JScrollPane responseScroll = new JScrollPane(aiOutputArea);
        responseScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                "AI RESPONSE"));
        center.add(responseScroll, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel questionPanel = new JPanel(new BorderLayout(10, 0));
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 207, 222), 1),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel questionLabel = new JLabel("YOUR QUESTION:", createNavIcon("ai"), SwingConstants.LEFT);
        questionLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        questionLabel.setForeground(new Color(18, 52, 86));
        questionPanel.add(questionLabel, BorderLayout.WEST);

        aiQuestionField = new JTextField("What should I study today?");
        aiQuestionField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionPanel.add(aiQuestionField, BorderLayout.CENTER);

        askAIButton = createBigButton("✨  ASK AI");
        askAIButton.setPreferredSize(new Dimension(140, 44));
        questionPanel.add(askAIButton, BorderLayout.EAST);

        panel.add(questionPanel, BorderLayout.SOUTH);
        askAIButton.addActionListener(e -> askAI());
        aiQuestionField.addActionListener(e -> askAI());

        return panel;
    }




    // =========================================================
    // AI INSIGHTS / SMART PLANNER PANEL
    // =========================================================

    private JPanel createAIInsightsPanel() {

        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(new Color(239, 244, 249));
        page.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel hero = new JPanel(new BorderLayout(8, 4));
        hero.setBackground(new Color(18, 52, 86));
        hero.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel heading = new JLabel(
                "AI LAB  •  ADVANCED GOAL INTELLIGENCE",
                createNavIcon("insights"),
                SwingConstants.LEFT
        );
        heading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        heading.setForeground(Color.WHITE);
        heading.setIconTextGap(10);

        JLabel sub = new JLabel(
                "Use your real goal, timer, roadmap and performance data to generate practical next actions.",
                SwingConstants.LEFT
        );
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(190, 211, 229));

        JPanel heroText = new JPanel(new BorderLayout(0, 4));
        heroText.setOpaque(false);
        heroText.add(heading, BorderLayout.NORTH);
        heroText.add(sub, BorderLayout.SOUTH);
        hero.add(heroText, BorderLayout.CENTER);
        page.add(hero, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(239, 244, 249));

        JPanel metrics = new JPanel(new GridLayout(1, 4, 10, 10));
        metrics.setOpaque(false);
        metrics.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel healthCard = insightMetricCard("GOAL HEALTH", "0%");
        goalHealthLabel = (JLabel) healthCard.getClientProperty("value");
        goalHealthBar = (JProgressBar) healthCard.getClientProperty("bar");
        metrics.add(healthCard);

        JPanel execCard = insightMetricCard("EXECUTION SCORE", "0%");
        executionLabel = (JLabel) execCard.getClientProperty("value");
        executionBar = (JProgressBar) execCard.getClientProperty("bar");
        metrics.add(execCard);

        JPanel consistencyCard = insightMetricCard("7-DAY CONSISTENCY", "0%");
        insightConsistencyLabel = (JLabel) consistencyCard.getClientProperty("value");
        metrics.add(consistencyCard);

        JPanel roadmapCard = insightMetricCard("ROADMAP STATUS", "0%");
        insightRoadmapLabel = (JLabel) roadmapCard.getClientProperty("value");
        metrics.add(roadmapCard);

        content.add(metrics);
        content.add(Box.createVerticalStrut(12));

        // WHAT-IF CARD
        JPanel scenarioCard = new JPanel(new BorderLayout(10, 8));
        scenarioCard.setBackground(Color.WHITE);
        scenarioCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 12, 14)));
        scenarioCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel scenarioTitle = new JLabel(
                "WHAT-IF SIMULATOR",
                createNavIcon("analyze"),
                SwingConstants.LEFT
        );
        scenarioTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        scenarioTitle.setForeground(new Color(18, 52, 86));

        JPanel scenarioRow = new JPanel(new BorderLayout(10, 0));
        scenarioRow.setOpaque(false);
        whatIfField = new JTextField("What if I study 5 hours every day?");
        whatIfField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JButton simulate = createBigButton("SIMULATE WHAT-IF");
        simulate.setPreferredSize(new Dimension(205, 42));
        scenarioRow.add(whatIfField, BorderLayout.CENTER);
        scenarioRow.add(simulate, BorderLayout.EAST);

        scenarioCard.add(scenarioTitle, BorderLayout.NORTH);
        scenarioCard.add(scenarioRow, BorderLayout.CENTER);
        content.add(scenarioCard);
        content.add(Box.createVerticalStrut(12));

        // FEATURE ACTIONS CARD
        JPanel actionCard = new JPanel(new BorderLayout(8, 10));
        actionCard.setBackground(Color.WHITE);
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 14, 14)));
        actionCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel actionTitle = new JLabel(
                "13 ADVANCED AI FEATURES",
                createNavIcon("ai"),
                SwingConstants.LEFT
        );
        actionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionTitle.setForeground(new Color(18, 52, 86));
        actionCard.add(actionTitle, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 4, 9, 9));
        actions.setOpaque(false);

        JButton goalTwin = createBigButton("AI GOAL TWIN");
        JButton bottleneck = createBigButton("BOTTLENECK DETECTOR");
        JButton dailyMission = createBigButton("AI DAILY MISSION");
        JButton adaptive = createBigButton("ADAPTIVE ROADMAP");
        JButton reschedule = createBigButton("AUTO RESCHEDULE");
        JButton deadlineRisk = createBigButton("DEADLINE RISK");
        JButton minimumChange = createBigButton("MINIMUM CHANGE");
        JButton windTunnel = createBigButton("GOAL WIND TUNNEL");
        JButton revision = createBigButton("SMART REVISION");
        JButton skillGap = createBigButton("SKILL GAP ANALYZER");
        JButton focusQuality = createBigButton("FOCUS QUALITY");
        JButton recovery = createBigButton("RECOVERY PLANNER");
        JButton achievement = createBigButton("ACHIEVEMENTS");

        actions.add(goalTwin);
        actions.add(bottleneck);
        actions.add(dailyMission);
        actions.add(adaptive);
        actions.add(reschedule);
        actions.add(deadlineRisk);
        actions.add(minimumChange);
        actions.add(windTunnel);
        actions.add(revision);
        actions.add(skillGap);
        actions.add(focusQuality);
        actions.add(recovery);
        actions.add(achievement);

        JScrollPane actionScroll = new JScrollPane(actions);
        actionScroll.setBorder(BorderFactory.createEmptyBorder());
        actionScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        actionScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        actionScroll.getVerticalScrollBar().setUnitIncrement(14);
        actionScroll.setPreferredSize(new Dimension(900, 165));
        actionCard.add(actionScroll, BorderLayout.CENTER);

        content.add(actionCard);
        content.add(Box.createVerticalStrut(12));

        // RESULT CARD
        JPanel resultCard = new JPanel(new BorderLayout());
        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 12, 14)));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resultTitle = new JLabel(
                "AI RESULT",
                createNavIcon("status"),
                SwingConstants.LEFT
        );
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultTitle.setForeground(new Color(18, 52, 86));

        aiInsightsArea = new JTextArea();
        aiLabOutputArea = aiInsightsArea;
        aiInsightsArea.setEditable(false);
        aiInsightsArea.setLineWrap(true);
        aiInsightsArea.setWrapStyleWord(true);
        aiInsightsArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        aiInsightsArea.setMargin(new Insets(14, 14, 14, 14));
        aiInsightsArea.setBackground(new Color(248, 250, 252));
        aiInsightsArea.setForeground(new Color(35, 48, 62));
        aiInsightsArea.setText(
                "SMART AI LAB\n\n"
                + "Create a goal first, then select any feature above.\n\n"
                + "The AI uses your stored goal, timer sessions, roadmap, progress, "
                + "consistency and timeline data."
        );

        resultCard.add(resultTitle, BorderLayout.NORTH);
        resultCard.add(new JScrollPane(aiInsightsArea), BorderLayout.CENTER);
        content.add(resultCard);

        JScrollPane pageScroll = new JScrollPane(content);
        pageScroll.setBorder(BorderFactory.createEmptyBorder());
        pageScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        page.add(pageScroll, BorderLayout.CENTER);

        goalTwin.addActionListener(e -> runGoalTwin());
        bottleneck.addActionListener(e -> runBottleneckDetector());
        dailyMission.addActionListener(e -> runDailyMission());
        adaptive.addActionListener(e -> runAdaptiveRoadmap());
        reschedule.addActionListener(e -> runAutoReschedule());
        deadlineRisk.addActionListener(e -> runDeadlineRisk());
        minimumChange.addActionListener(e -> runMinimumChange());
        windTunnel.addActionListener(e -> runGoalWindTunnel());
        revision.addActionListener(e -> runSmartRevision());
        skillGap.addActionListener(e -> runSkillGapAnalyzer());
        focusQuality.addActionListener(e -> runFocusQuality());
        recovery.addActionListener(e -> runRecoveryCheck());
        achievement.addActionListener(e -> showAchievements());
        simulate.addActionListener(e -> runWhatIfSimulation());
        whatIfField.addActionListener(e -> runWhatIfSimulation());

        return page;
    }

    private JPanel insightMetricCard(String title, String initialValue) {

        JPanel card = new JPanel(new BorderLayout(8, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(new Color(78, 96, 115));

        JLabel valueLabel = new JLabel(initialValue, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(new Color(18, 82, 130));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setStringPainted(true);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bar.setForeground(new Color(37, 137, 99));
        bar.setBackground(new Color(229, 235, 241));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(220, 20));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);

        card.putClientProperty("value", valueLabel);
        card.putClientProperty("bar", bar);

        return card;
    }


    // =========================================================
    // AI INSIGHTS / SMART PLANNER PANEL
    // =========================================================


    // =========================================================
    // AI LAB ACTIONS
    // =========================================================

    private void runDailyMission() {

        if (!requireGoal()) return;

        String prompt =
                buildAIContext()
                + """
                Create TODAY'S AI MISSION.

                Use the pending roadmap, today's target,
                recent activity and last-7-day performance.

                Rules:
                - Give 3 to 5 concrete tasks.
                - Total planned minutes must be at or below
                  today's target when a target exists.
                - Prioritize weak or overdue areas.
                - Include one short revision/check task.
                - Do not invent exam scores or external statistics.

                Format:
                TODAY'S AI MISSION
                1. Task — minutes — why
                2. Task — minutes — why
                ...
                END-OF-DAY CHECK
                """;

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                null
        );
    }

    private void runWeaknessDetector() {

        if (!requireGoal()) return;

        String breakdown =
                DatabaseManager.getLast7DaysBreakdown(
                        currentGoalId
                );

        String prompt =
                buildAIContext()
                + """
                Run a WEAKNESS DETECTOR.

                Based only on the stored activity breakdown,
                roadmap status and recent performance:
                - identify the top 3 areas needing attention;
                - explain the evidence for each;
                - give one action for each area;
                - explicitly distinguish low study time from low mastery,
                  because timer data alone cannot prove mastery.

                ACTIVITY BREAKDOWN:
                %s
                """.formatted(
                        breakdown
                );

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                null
        );
    }

    private void runAdaptiveRoadmap() {

        if (!requireGoal()) return;

        String prompt =
                buildAIContext()
                + """
                Act as an adaptive roadmap engine.

                Compare the user's current roadmap with recent performance.
                Return ONLY valid JSON:
                {
                  "reason": "string",
                  "add_topics": ["topic 1", "topic 2", "topic 3", "topic 4"]
                }

                Rules:
                - Add only missing, useful or reordered topics.
                - Do not claim mastery based only on time.
                - If no new topic is justified, return an empty array.
                - Maximum 6 new topics.
                """;

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                response -> {

                    try {

                        JSONObject result =
                                new JSONObject(
                                        extractJsonObject(response)
                                );

                        String reason =
                                result.optString(
                                        "reason",
                                        ""
                                );

                        JSONArray topics =
                                result.optJSONArray(
                                        "add_topics"
                                );

                        int added = 0;

                        if (topics != null) {

                            for (int i = 0;
                                    i < topics.length();
                                    i++) {

                                String topic =
                                        topics.optString(
                                                i,
                                                ""
                                        ).trim();

                                if (!topic.isEmpty()) {

                                    DatabaseManager.addRoadmapTopic(
                                            currentGoalId,
                                            topic
                                    );

                                    added++;
                                }
                            }
                        }

                        loadRoadmap();
                        updateAllProgress();

                        aiOutputSafeSet(
                                "ADAPTIVE ROADMAP UPDATED\n\n"
                                + "NEW TOPICS ADDED: "
                                + added
                                + "\n\n"
                                + "AI REASON\n"
                                + reason
                        );

                    } catch (Exception ex) {

                        aiOutputSafeSet(
                                "Adaptive roadmap AI response:\n\n"
                                + response
                                + "\n\nJSON parsing error: "
                                + ex.getMessage()
                        );
                    }
                }
        );
    }

    private void runAutoReschedule() {

        if (!requireGoal()) return;

        double health =
                calculateGoalHealth();

        String prompt =
                buildAIContext()
                + """
                Create an AUTO-RESCHEDULE PLAN for the next 7 days.

                Use the user's actual recent pace and current target.
                This is planning support, not a guarantee.

                Return ONLY valid JSON:
                {
                  "reason": "string",
                  "days": [
                    {"date":"YYYY-MM-DD","minutes":180,"focus":"topic or activity"},
                    ...
                  ]
                }

                Rules:
                - Include the next 7 calendar days.
                - Use realistic minutes based on recent actual performance.
                - Avoid sudden extreme increases.
                - The plan may move load from missed work into later days,
                  but should remain practical.
                - Prefer pending roadmap areas.
                - Do not invent external statistics.

                CURRENT GOAL HEALTH:
                %.1f
                """.formatted(
                        health
                );

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                response -> {

                    try {

                        JSONObject result =
                                new JSONObject(
                                        extractJsonObject(response)
                                );

                        JSONArray days =
                                result.optJSONArray(
                                        "days"
                                );

                        int saved = 0;

                        if (days != null) {

                            for (int i = 0;
                                    i < days.length();
                                    i++) {

                                JSONObject day =
                                        days.optJSONObject(i);

                                if (day == null) continue;

                                String dateText =
                                        day.optString(
                                                "date",
                                                ""
                                        );

                                int minutes =
                                        day.optInt(
                                                "minutes",
                                                0
                                        );

                                if (dateText.isEmpty() ||
                                        minutes <= 0) {
                                    continue;
                                }

                                LocalDate date =
                                        LocalDate.parse(
                                                dateText
                                        );

                                if (date.isBefore(
                                        LocalDate.now()
                                )) {
                                    continue;
                                }

                                DatabaseManager.saveDailyTarget(
                                        currentGoalId,
                                        date,
                                        minutes
                                );

                                saved++;
                            }
                        }

                        String reason =
                                result.optString(
                                        "reason",
                                        ""
                                );

                        updateAllProgress();

                        aiOutputSafeSet(
                                "AUTO RESCHEDULE COMPLETE\n\n"
                                + "FUTURE DAILY TARGETS UPDATED: "
                                + saved
                                + "\n\n"
                                + "REASON\n"
                                + reason
                                + "\n\n"
                                + "Note: future targets were generated as planning recommendations."
                        );

                    } catch (Exception ex) {

                        aiOutputSafeSet(
                                "Auto-reschedule AI response:\n\n"
                                + response
                                + "\n\nJSON parsing error: "
                                + ex.getMessage()
                        );
                    }
                }
        );
    }

    private void runWhyBehind() {

        if (!requireGoal()) return;

        runAIQuestion(
                "WHY AM I BEHIND? Compare today's target, recent consistency, "
                + "roadmap completion, total study and current goal timeline. "
                + "Give the 3 most concrete data-supported reasons and the next "
                + "three actions. Do not assume mastery from time alone.",
                null,
                aiInsightsArea
        );
    }

    private void runRecoveryCheck() {

        if (!requireGoal()) return;

        double execution =
                getExecutionScore();

        String prompt =
                buildAIContext()
                + """
                Run a RECOVERY CHECK.

                Analyze recent workload and execution patterns.
                Do not diagnose medical or mental-health conditions.

                Give:
                1. current workload signal;
                2. whether the next plan should maintain, reduce or rebalance load;
                3. a practical recovery-friendly study structure;
                4. a warning that timer data alone cannot determine health.

                Current execution score:
                %.1f%%
                """.formatted(
                        execution
                );

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                null
        );
    }

    private void runReferenceRequirements() {

        if (!requireGoal()) return;

        String benchmarkInfo =
                DatabaseManager.getBenchmarkInfo();

        String prompt =
                buildAIContext()
                + """
                Build a REFERENCE REQUIREMENTS profile for this goal.

                The user wants comparison against highly successful people
                and common requirements. Do NOT invent named-person statistics.

                Give:
                - common requirements or milestones for this goal;
                - which dimensions can be compared objectively;
                - what verified external benchmark data would be required;
                - a clear note when no verified external benchmark is stored.

                CURRENT STORED BENCHMARK INFORMATION:
                %s
                """.formatted(
                        benchmarkInfo
                );

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                null
        );
    }

    private void runWhatIfSimulation() {

        if (!requireGoal()) return;

        String scenario =
                whatIfField.getText().trim();

        if (scenario.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter a What-If scenario first."
            );

            whatIfField.requestFocus();
            return;
        }

        String prompt =
                buildAIContext()
                + """
                Run a WHAT-IF SIMULATION.

                Scenario:
                %s

                Explain:
                1. what would change in the user's timeline;
                2. what would likely change in daily/weekly load;
                3. which roadmap areas would need adjustment;
                4. what is still unknown.

                Use scenario reasoning only.
                Do not state uncertain outcomes as guarantees.
                """.formatted(
                        scenario
                );

        runAIAsync(
                prompt,
                null,
                aiInsightsArea,
                null
        );
    }

    // =========================================================
    // ADVANCED AI LAB ACTIONS
    // =========================================================

    private void runGoalTwin() {
        if (!requireGoal()) return;
        String prompt = buildAIContext() + """

                Create an AI GOAL TWIN snapshot.

                Explain the current state of this user's goal using:
                - goal and deadline
                - actual study time
                - roadmap completion
                - consistency and streak
                - activity history

                Return:
                1. Goal status
                2. Strong areas
                3. Current bottleneck
                4. Main risk
                5. Next 3 actions

                Do not invent data or claim mastery from time alone.
                """;
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runBottleneckDetector() {
        if (!requireGoal()) return;
        String prompt = buildAIContext() + """

                Run a BOTTLENECK DETECTOR.

                Choose the most important current bottleneck from:
                TIME, CONSISTENCY, ROADMAP COVERAGE, PRACTICE, REVISION,
                DEADLINE PRESSURE, or OTHER.

                Give the evidence from the stored data, explain uncertainty,
                and give the smallest practical next action.
                Do not infer knowledge mastery from timer minutes alone.
                """;
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runDeadlineRisk() {
        if (!requireGoal()) return;
        String prompt = buildAIContext() + """

                Run a DEADLINE RISK CHECK.

                Compare current progress, actual study pace, roadmap progress,
                remaining time and target. Explain whether the current plan has
                a low, medium or high planning risk based only on the available
                data. Do not present this as a guaranteed prediction.

                Give:
                1. evidence;
                2. gap;
                3. practical adjustment;
                4. what additional data would improve the estimate.
                """;
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runMinimumChange() {
        if (!requireGoal()) return;
        String prompt = buildAIContext() + """

                Find the MINIMUM CHANGE that could improve progress toward the
                current goal without recommending an extreme workload.

                Consider recent actual pace, today's target, roadmap status and
                deadline. Give one primary change and one backup option.
                Explain the calculation or reasoning using the supplied data.
                """;
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runGoalWindTunnel() {
        if (!requireGoal()) return;
        String prompt = buildAIContext() + """

                Run a GOAL WIND TUNNEL using three scenarios:
                A) current pace;
                B) +30 minutes per day;
                C) +60 minutes per day.

                Compare expected workload, target gap and roadmap pressure.
                This is scenario analysis, not a guaranteed forecast.
                Do not declare a winner; let the user compare the trade-offs.
                """;
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runSmartRevision() {
        if (!requireGoal()) return;
        String roadmap = DatabaseManager.getRoadmap(currentGoalId);
        String prompt = buildAIContext() + """

                Create a SMART REVISION PLAN.

                Use the roadmap and activity history. Suggest which completed
                or previously studied areas should be revised next and why.
                Give a simple 7-day revision structure. Do not claim mastery
                without assessment evidence.

                ROADMAP:
                %s
                """.formatted(roadmap);
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runSkillGapAnalyzer() {
        if (!requireGoal()) return;
        String roadmap = DatabaseManager.getRoadmap(currentGoalId);
        String prompt = buildAIContext() + """

                Run a SKILL / KNOWLEDGE GAP ANALYSIS.

                Compare the goal roadmap requirements with the user's recorded
                activity and completion data. Separate:
                - not started;
                - started but incomplete;
                - completed by activity tracking.

                Do not claim that completion proves mastery.

                ROADMAP:
                %s
                """.formatted(roadmap);
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void runFocusQuality() {
        if (!requireGoal()) return;
        String history = DatabaseManager.getActivityHistory(currentGoalId);
        String prompt = buildAIContext() + """

                Analyze FOCUS QUALITY using only observable timer/session data.

                Look for session length, consistency, planned-vs-actual time,
                pauses/stops if available, and repeated short sessions.
                Do NOT claim to measure attention or diagnose distraction.

                Give:
                1. observable focus pattern;
                2. a simple quality indicator such as Strong / Mixed / Needs Review;
                3. one practical improvement.

                ACTIVITY HISTORY:
                %s
                """.formatted(history);
        runAIAsync(prompt, null, aiInsightsArea, null);
    }

    private void showAchievements() {
        if (!requireGoal()) return;

        int streak = DatabaseManager.getCurrentStudyStreak(currentGoalId);
        int completed = DatabaseManager.getCompletedRoadmapTopics(currentGoalId);
        int totalTopics = DatabaseManager.getTotalRoadmapTopics(currentGoalId);
        int totalSeconds = DatabaseManager.getTotalStudySeconds(currentGoalId);
        double consistency = DatabaseManager.getLast7DaysConsistency(currentGoalId);

        StringBuilder out = new StringBuilder();
        out.append("PERSONAL ACHIEVEMENTS\n\n");
        out.append(streak >= 7 ? "🏆 7-DAY STREAK UNLOCKED\n" : "🔒 7-DAY STREAK — ").append(Math.max(0, 7 - streak)).append(" days remaining\n");
        out.append(totalSeconds >= 36000 ? "🏆 10-HOUR STUDY MILESTONE\n" : "🔒 10-HOUR MILESTONE — ").append(String.format("%.1f", Math.max(0, 10.0 - totalSeconds / 3600.0))).append(" hours remaining\n");
        out.append(totalTopics > 0 && completed == totalTopics ? "🏆 ROADMAP COMPLETED\n" : "🔒 ROADMAP COMPLETION — ").append(completed).append(" / ").append(totalTopics).append(" topics\n");
        out.append(consistency >= 80 ? "🏆 80% CONSISTENCY\n" : "🔒 80% CONSISTENCY — current: ").append(String.format("%.1f%%", consistency)).append("\n");
        out.append("\nThese achievements are calculated from your stored app data.");
        aiInsightsArea.setText(out.toString());
    }

    // =========================================================
    // AI RESOURCE CENTER
    // =========================================================

    private JPanel createAIResourceCenterPanel() {

        JPanel page = new JPanel(new BorderLayout(14, 14));
        page.setBackground(new Color(239, 244, 249));
        page.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel hero = new JPanel(new BorderLayout(8, 4));
        hero.setBackground(new Color(18, 52, 86));
        hero.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel heading = new JLabel(
                "AI RESOURCE CENTER",
                createNavIcon("resources"),
                SwingConstants.LEFT
        );
        heading.setFont(new Font("Segoe UI", Font.BOLD, 25));
        heading.setForeground(Color.WHITE);
        heading.setIconTextGap(10);

        JLabel sub = new JLabel(
                "Select your goal and let AI find relevant learning resources.",
                SwingConstants.LEFT
        );
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(new Color(190, 211, 229));

        JPanel heroText = new JPanel(new BorderLayout(0, 4));
        heroText.setOpaque(false);
        heroText.add(heading, BorderLayout.NORTH);
        heroText.add(sub, BorderLayout.SOUTH);
        hero.add(heroText, BorderLayout.CENTER);
        page.add(hero, BorderLayout.NORTH);

        JPanel topCard = new JPanel(new BorderLayout(12, 8));
        topCard.setBackground(Color.WHITE);
        topCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel goalTitle = new JLabel(
                "CURRENT GOAL",
                createNavIcon("goal"),
                SwingConstants.LEFT
        );
        goalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        goalTitle.setForeground(new Color(18, 52, 86));

        resourceGoalLabel = new JLabel("Create a goal first", SwingConstants.LEFT);
        resourceGoalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        resourceGoalLabel.setForeground(new Color(37, 99, 235));

        generateResourcesButton = createBigButton("GENERATE RESOURCES WITH AI");
        generateResourcesButton.setPreferredSize(new Dimension(260, 46));
        generateResourcesButton.addActionListener(e -> generateAIResources());

        JPanel goalInfo = new JPanel(new BorderLayout(0, 4));
        goalInfo.setOpaque(false);
        goalInfo.add(goalTitle, BorderLayout.NORTH);
        goalInfo.add(resourceGoalLabel, BorderLayout.CENTER);

        topCard.add(goalInfo, BorderLayout.CENTER);
        topCard.add(generateResourcesButton, BorderLayout.EAST);

        page.add(topCard, BorderLayout.CENTER);

        JPanel resultCard = new JPanel(new BorderLayout());
        resultCard.setBackground(Color.WHITE);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel resultTitle = new JLabel(
                "RECOMMENDED RESOURCES",
                createNavIcon("resources"),
                SwingConstants.LEFT
        );
        resultTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resultTitle.setForeground(new Color(18, 52, 86));

        resourceOutputArea = new JTextArea();
        resourceOutputArea.setEditable(false);
        resourceOutputArea.setLineWrap(true);
        resourceOutputArea.setWrapStyleWord(true);
        resourceOutputArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        resourceOutputArea.setMargin(new Insets(16, 16, 16, 16));
        resourceOutputArea.setBackground(new Color(248, 250, 252));
        resourceOutputArea.setForeground(new Color(35, 48, 62));
        resourceOutputArea.setText(
                "AI RESOURCE CENTER\n\n"
                + "Create or load a goal first.\n\n"
                + "Then click GENERATE RESOURCES WITH AI.\n\n"
                + "AI will organize relevant books, videos, official websites, "
                + "practice material, previous-year papers, mock tests and notes."
        );

        resultCard.add(resultTitle, BorderLayout.NORTH);
        resultCard.add(new JScrollPane(resourceOutputArea), BorderLayout.CENTER);

        page.add(resultCard, BorderLayout.SOUTH);

        // Keep the result area large when the page is resized.
        page.remove(topCard);
        page.remove(resultCard);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(topCard, BorderLayout.NORTH);
        center.add(resultCard, BorderLayout.CENTER);
        page.add(center, BorderLayout.CENTER);

        return page;
    }

    private void generateAIResources() {

        if (!requireGoal()) {
            return;
        }

        String goal = DatabaseManager.getGoalName(currentGoalId);

        if (goal == null || goal.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Create a goal first."
            );
            return;
        }

        String roadmap = DatabaseManager.getRoadmap(currentGoalId);

        String prompt = buildAIContext() + """

                You are the AI Resource Center of Smart Time Coach.

                USER GOAL:
                %s

                ROADMAP:
                %s

                Generate a practical resource list specifically for this goal.

                Include these categories when relevant:
                1. Books / textbooks
                2. YouTube lectures or channels
                3. Official websites / documentation
                4. Practice material
                5. Previous-year papers
                6. Mock tests
                7. Notes / reference material
                8. Revision resources

                IMPORTANT RULES:
                - Resources must match the user's actual goal.
                - Do not give a generic list.
                - Prefer official sources and well-known educational sources.
                - NEVER invent a URL.
                - If you cannot verify an exact URL, write the source name without a URL.
                - Clearly mark official sources as OFFICIAL.
                - Keep the list practical and not unnecessarily long.
                - Explain in one short line why each resource is useful.

                Use this format:

                AI RESOURCE PLAN

                GOAL:
                %s

                BOOKS
                - Resource — why it helps

                VIDEOS / LECTURES
                - Resource — why it helps

                OFFICIAL SOURCES
                - Resource — URL if known with confidence — why it helps

                PRACTICE / PYQs
                - Resource — why it helps

                MOCK TESTS
                - Resource — why it helps

                NOTES / REVISION
                - Resource — why it helps

                START HERE
                Give the user the first 3 resources they should use.
                """.formatted(
                        goal,
                        roadmap,
                        goal
                );

        runAIAsync(
                prompt,
                generateResourcesButton,
                resourceOutputArea,
                null
        );
    }

    // =========================================================
    // AI UTILITY METHODS
    // =========================================================

    private boolean requireGoal() {

        if (currentGoalId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Create a goal first."
            );

            return false;
        }

        return true;
    }

    private void runAIAsync(
            String prompt,
            JButton busyButton,
            JTextArea outputArea,
            java.util.function.Consumer<String> callback
    ) {

        if (outputArea != null) {

            outputArea.setText(
                    "AI IS ANALYZING...\n\nPlease wait."
            );
        }

        if (busyButton != null) {

            busyButton.setEnabled(false);
        }

        SwingWorker<String, Void> worker =
                new SwingWorker<>() {

                    @Override
                    protected String doInBackground() {

                        return AIManager.askAI(
                                prompt
                        );
                    }

                    @Override
                    protected void done() {

                        String response;

                        try {

                            response = get();

                        } catch (Exception e) {

                            response =
                                    "AI Error:\n"
                                    + e.getMessage();
                        }

                        if (callback != null) {

                            try {

                                callback.accept(
                                        response
                                );

                            } catch (Exception e) {

                                response +=
                                        "\n\nCallback Error: "
                                        + e.getMessage();
                            }

                        } else if (outputArea != null) {

                            outputArea.setText(
                                    response
                            );
                        }

                        if (busyButton != null) {

                            busyButton.setEnabled(
                                    true
                            );
                        }
                    }
                };

        worker.execute();
    }

    private void aiOutputSafeSet(
            String text
    ) {

        if (aiInsightsArea != null) {

            aiInsightsArea.setText(
                    text
            );

            aiInsightsArea.setCaretPosition(
                    0
            );
        }

        if (aiOutputArea != null) {

            aiOutputArea.setText(
                    text
            );

            aiOutputArea.setCaretPosition(
                    0
            );
        }
    }

    private String extractJsonObject(
            String response
    ) {

        if (response == null) {
            return "{}";
        }

        String cleaned =
                response
                        .replace(
                                "```json",
                                ""
                        )
                        .replace(
                                "```",
                                ""
                        )
                        .trim();

        int start =
                cleaned.indexOf('{');

        int end =
                cleaned.lastIndexOf('}');

        if (start >= 0 &&
                end > start) {

            return cleaned.substring(
                    start,
                    end + 1
            );
        }

        return cleaned;
    }

    private String joinJsonArray(
            JSONArray array
    ) {

        if (array == null ||
                array.length() == 0) {

            return "None supplied.";
        }

        StringBuilder sb =
                new StringBuilder();

        for (int i = 0;
                i < array.length();
                i++) {

            String value =
                    array.optString(
                            i,
                            ""
                    ).trim();

            if (value.isEmpty()) continue;

            sb.append("• ")
              .append(value)
              .append("\n");
        }

        return sb.toString();
    }

    private int parsePositiveInt(
            String text,
            int fallback
    ) {

        try {

            int value =
                    Integer.parseInt(
                            text == null
                            ? ""
                            : text.trim()
                    );

            return value > 0
                    ? value
                    : fallback;

        } catch (Exception e) {

            return fallback;
        }
    }

    private double parsePositiveDouble(
            String text,
            double fallback
    ) {

        try {

            double value =
                    Double.parseDouble(
                            text == null
                            ? ""
                            : text.trim()
                    );

            return value > 0
                    ? value
                    : fallback;

        } catch (Exception e) {

            return fallback;
        }
    }

    // =========================================================
    // BENCHMARK PANEL
    // =========================================================

    private JPanel createBenchmarkPanel() {

        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(new Color(239, 244, 249));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));

        JLabel title = new JLabel("SMART BENCHMARK ENGINE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(18, 52, 86));
        title.setIcon(createNavIcon("benchmark"));
        title.setIconTextGap(10);
        panel.add(title, BorderLayout.NORTH);

        JPanel statusCard = new JPanel(new BorderLayout(12, 8));
        statusCard.setBackground(Color.WHITE);
        statusCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                new EmptyBorder(12, 18, 12, 18)));

        benchmarkStatusLabel = new JLabel("CREATE A GOAL TO START", SwingConstants.CENTER);
        benchmarkStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        benchmarkStatusLabel.setForeground(new Color(18, 52, 86));
        statusCard.add(benchmarkStatusLabel, BorderLayout.NORTH);

        benchmarkProgressBar = new JProgressBar(0, 100);
        benchmarkProgressBar.setStringPainted(true);
        benchmarkProgressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        benchmarkProgressBar.setForeground(new Color(42, 110, 160));
        benchmarkProgressBar.setBackground(new Color(229, 235, 241));
        benchmarkProgressBar.setPreferredSize(new Dimension(600, 30));
        statusCard.add(benchmarkProgressBar, BorderLayout.CENTER);
        panel.add(statusCard, BorderLayout.NORTH);

        benchmarkArea = new JTextArea();
        benchmarkArea.setEditable(false);
        benchmarkArea.setLineWrap(true);
        benchmarkArea.setWrapStyleWord(true);
        benchmarkArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        benchmarkArea.setMargin(new Insets(18, 18, 18, 18));
        benchmarkArea.setBackground(Color.WHITE);
        benchmarkArea.setText(
                "SMART BENCHMARK ANALYSIS\n\n"
                + "This engine compares your actual progress with your own goal timeline.\n\n"
                + "NO VERIFIED EXTERNAL BENCHMARK DATA IS CONFIGURED YET.\n"
                + "Therefore, the system will not invent world-aspirant statistics.\n\n"
                + "Create a goal, set a deadline and record timer sessions to generate your personal benchmark report.");

        JScrollPane scroll = new JScrollPane(benchmarkArea);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 214, 226), 1),
                "YOUR BENCHMARK REPORT"));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 7));
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 58));
        JButton analyze = createBigButton("ANALYZE BENCHMARK");
        JButton refresh = createBigButton("↻ REFRESH");
        bottom.add(analyze);
        bottom.add(refresh);
        panel.add(bottom, BorderLayout.SOUTH);
        analyze.addActionListener(e -> loadBenchmark());
        refresh.addActionListener(e -> loadBenchmark());

        return panel;
    }



    // =========================================================
    // SAVE GOAL
    // =========================================================

    private void saveGoal() {

        try {

            String name = goalNameField.getText().trim();

            double hours =
                    Double.parseDouble(
                            targetHoursField.getText().trim()
                    );

            int days =
                    Integer.parseInt(
                            deadlineField.getText().trim()
                    );

            if (name.isEmpty() || hours <= 0 || days <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter a valid goal name, target hours and deadline."
                );

                return;
            }

            int targetMinutes =
                    (int) Math.round(hours * 60);

            int id =
                    DatabaseManager.saveGoal(
                            name,
                            targetMinutes,
                            days
                    );

            if (id != -1) {

                currentGoalId = id;

                goalLabel.setText("Current Goal: " + name);
                deadlineLabel.setText("Remaining Days: " + days);

                if (resourceGoalLabel != null) {
                    resourceGoalLabel.setText(name);
                }

                loadRoadmap();
                updateAllProgress();

                JOptionPane.showMessageDialog(
                        this,
                        "Goal saved successfully."
                );
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid numbers for target hours and deadline."
            );
        }
    }

    // =========================================================
    // AI GOAL CREATION
    // =========================================================

    private void createGoalWithAI() {

        final String requestedGoal =
                goalNameField.getText().trim();

        if (requestedGoal.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter only your goal first.\n\nExample: IIT Preparation",
                    "Goal Required",
                    JOptionPane.WARNING_MESSAGE
            );

            goalNameField.requestFocus();
            return;
        }

        final double manualHours = parsePositiveDouble(targetHoursField.getText(), 0);
        final int manualDays = parsePositiveInt(deadlineField.getText(), 0);
        final int manualDaily = parsePositiveInt(dailyTargetField.getText(), 0);

        aiCreateGoalButton.setEnabled(false);

        aiOutputSafeSet(
                "AI GOAL SETUP\n\n"
                + "Analyzing: " + requestedGoal + "\n\n"
                + "AI is building:\n"
                + "• Goal requirements\n"
                + "• Timeline\n"
                + "• Daily target\n"
                + "• Adaptive roadmap\n"
                + "• Reference dimensions"
        );

        String prompt =
                """
                You are the goal-planning engine of Smart Time Coach.

                The user will provide ONLY a goal name.
                Build a practical first version of the plan.

                IMPORTANT:
                - Return ONLY valid JSON. No markdown. No code fences.
                - Do not invent claims about named successful people.
                - Do not invent external benchmark statistics.
                - "requirements" means common capabilities, subjects, skills, exams,
                  milestones or evidence normally relevant to the goal.
                - "common_benchmark_dimensions" means what should be compared later
                  when verified external data is available.
                - Target hours, deadline and daily target are planning recommendations,
                  not guarantees.
                - Roadmap should be concrete and sequential.
                - Keep roadmap between 10 and 24 items.

                JSON schema:
                {
                  "goal_name": "string",
                  "target_hours": 120.0,
                  "deadline_days": 120,
                  "daily_target_minutes": 180,
                  "roadmap": ["topic 1", "topic 2"],
                  "requirements": ["requirement 1", "requirement 2"],
                  "common_benchmark_dimensions": ["dimension 1", "dimension 2"],
                  "planning_note": "string"
                }

                USER GOAL:
                %s

                OPTIONAL USER VALUES (use them when positive, otherwise recommend):
                manual_target_hours=%s
                manual_deadline_days=%s
                manual_daily_target_minutes=%s
                """.formatted(
                        requestedGoal,
                        manualHours > 0 ? String.valueOf(manualHours) : "not provided",
                        manualDays > 0 ? String.valueOf(manualDays) : "not provided",
                        manualDaily > 0 ? String.valueOf(manualDaily) : "not provided"
                );

        runAIAsync(
                prompt,
                aiCreateGoalButton,
                aiOutputArea,
                response -> {

                    try {

                        JSONObject plan =
                                new JSONObject(
                                        extractJsonObject(response)
                                );

                        String name =
                                plan.optString(
                                        "goal_name",
                                        requestedGoal
                                ).trim();

                        double hours =
                                plan.optDouble(
                                        "target_hours",
                                        manualHours
                                );

                        int days =
                                plan.optInt(
                                        "deadline_days",
                                        manualDays
                                );

                        int daily =
                                plan.optInt(
                                        "daily_target_minutes",
                                        manualDaily
                                );

                        if (hours <= 0) {
                            hours = 120;
                        }

                        if (days <= 0) {
                            days = 120;
                        }

                        if (daily <= 0) {
                            daily = Math.max(
                                    60,
                                    (int) Math.round(
                                            hours * 60.0 / days
                                    )
                            );
                        }

                        int id =
                                DatabaseManager.saveGoal(
                                        name,
                                        (int) Math.round(hours * 60),
                                        days
                                );

                        if (id == -1) {
                            aiOutputSafeSet(
                                    "AI created the plan, but the goal could not be saved."
                            );
                            return;
                        }

                        currentGoalId = id;

                        DatabaseManager.saveDailyTarget(
                                currentGoalId,
                                LocalDate.now(),
                                daily
                        );

                        JSONArray roadmap =
                                plan.optJSONArray("roadmap");

                        int roadmapAdded = 0;

                        if (roadmap != null) {

                            for (int i = 0;
                                    i < roadmap.length();
                                    i++) {

                                String topic =
                                        roadmap.optString(i, "").trim();

                                if (!topic.isEmpty()) {

                                    DatabaseManager.addRoadmapTopic(
                                            currentGoalId,
                                            topic
                                    );

                                    roadmapAdded++;
                                }
                            }
                        }

                        goalNameField.setText(name);
                        targetHoursField.setText(
                                String.format(
                                        "%.1f",
                                        hours
                                )
                        );
                        deadlineField.setText(
                                String.valueOf(days)
                        );
                        dailyTargetField.setText(
                                String.valueOf(daily)
                        );

                        goalLabel.setText(
                                "Current Goal: " + name
                        );

                        deadlineLabel.setText(
                                "Remaining Days: " + days
                        );

                        dailyTargetLabel.setText(
                                "Today's Target: "
                                + daily
                                + " min"
                        );

                        loadRoadmap();
                        updateAllProgress();
                        updateAIInsightsMetrics();

                        String requirements =
                                joinJsonArray(
                                        plan.optJSONArray("requirements")
                                );

                        String dimensions =
                                joinJsonArray(
                                        plan.optJSONArray(
                                                "common_benchmark_dimensions"
                                        )
                                );

                        String note =
                                plan.optString(
                                        "planning_note",
                                        ""
                                );

                        aiOutputSafeSet(
                                "AI GOAL CREATED SUCCESSFULLY\n\n"
                                + "GOAL: " + name + "\n"
                                + "AI TARGET: "
                                + String.format("%.1f", hours)
                                + " hours\n"
                                + "AI TIMELINE: "
                                + days
                                + " days\n"
                                + "TODAY'S TARGET: "
                                + daily
                                + " minutes\n"
                                + "ROADMAP ITEMS ADDED: "
                                + roadmapAdded
                                + "\n\n"
                                + "COMMON REQUIREMENTS\n"
                                + requirements
                                + "\n\n"
                                + "REFERENCE DIMENSIONS\n"
                                + dimensions
                                + "\n\n"
                                + "AI PLANNING NOTE\n"
                                + note
                        );

                    } catch (Exception ex) {

                        aiOutputSafeSet(
                                "AI returned a response, but it was not in the required JSON format.\n\n"
                                + "Raw response:\n"
                                + response
                                + "\n\nError: "
                                + ex.getMessage()
                        );
                    }
                }
        );
    }

    // =========================================================
    // SAVE DAILY TARGET
    // =========================================================

    private void saveDailyTarget() {

        if (currentGoalId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Create a goal first."
            );

            return;
        }

        try {

            int minutes =
                    Integer.parseInt(
                            dailyTargetField
                                    .getText()
                                    .trim()
                    );

            if (minutes <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Target must be greater than 0."
                );

                return;
            }

            DatabaseManager.saveDailyTarget(
                    currentGoalId,
                    LocalDate.now(),
                    minutes
            );

            dailyTargetLabel.setText(
                    "Today's Target: "
                            + minutes
                            + " min"
            );

            updateAllProgress();

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter valid minutes."
            );
        }
    }

    // =========================================================
    // START / RESUME TIMER
    // =========================================================

    private void startTimer() {

        if (currentGoalId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Create a goal first."
            );

            return;
        }

        // First Start: read the duration.
        if (remainingSeconds == 0) {

            try {

                int minutes =
                        Integer.parseInt(
                                durationField
                                        .getText()
                                        .trim()
                        );

                if (minutes <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Enter a duration greater than 0."
                    );

                    return;
                }

                plannedSeconds = minutes * 60;
                remainingSeconds = plannedSeconds;
                elapsedSeconds = 0;

                timerStartTime = LocalDateTime.now();

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter valid duration."
                );

                return;
            }
        }

        // Create a real-time deadline for the remaining duration.
        // This is independent of whether the Swing window has focus.
        timerEndNanos =
                System.nanoTime()
                        + (remainingSeconds * 1_000_000_000L);

        timerRunning = true;

        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);

        if (swingTimer == null) {

            // UI refresh only. The actual time is calculated from
            // System.nanoTime(), so missed UI ticks cannot lose time.
            swingTimer =
                    new Timer(
                            250,
                            e -> timerTick()
                    );

            swingTimer.setCoalesce(true);
        }

        if (!swingTimer.isRunning()) {
            swingTimer.start();
        }

        updateTimerLabel();
    }

    // =========================================================
    // TIMER TICK
    // =========================================================

    private void timerTick() {

        if (!timerRunning) {
            return;
        }

        long remainingNanos =
                timerEndNanos - System.nanoTime();

        if (remainingNanos <= 0) {

            remainingSeconds = 0;
            elapsedSeconds = plannedSeconds;

            if (swingTimer != null) {
                swingTimer.stop();
            }

            timerRunning = false;

            saveCurrentSession("Completed");

            resetTimer();

            updateAllProgress();

            JOptionPane.showMessageDialog(
                    this,
                    "Timer completed!"
            );

            return;
        }

        remainingSeconds =
                (int) Math.ceil(
                        remainingNanos / 1_000_000_000.0
                );

        elapsedSeconds =
                Math.max(
                        0,
                        plannedSeconds - remainingSeconds
                );

        updateTimerLabel();
    }

    // =========================================================
    // PAUSE
    // =========================================================

    private void pauseTimer() {

        if (!timerRunning) {
            return;
        }

        // Capture exact remaining time before pausing.
        long remainingNanos =
                timerEndNanos - System.nanoTime();

        remainingSeconds =
                Math.max(
                        0,
                        (int) Math.ceil(
                                remainingNanos
                                        / 1_000_000_000.0
                        )
                );

        elapsedSeconds =
                Math.max(
                        0,
                        plannedSeconds - remainingSeconds
                );

        timerRunning = false;

        if (swingTimer != null) {
            swingTimer.stop();
        }

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);

        updateTimerLabel();
    }

    // =========================================================
    // STOP
    // =========================================================

    private void stopTimer() {

        if (timerRunning) {

            // Capture exact elapsed time before saving.
            long remainingNanos =
                    timerEndNanos - System.nanoTime();

            remainingSeconds =
                    Math.max(
                            0,
                            (int) Math.ceil(
                                    remainingNanos
                                            / 1_000_000_000.0
                            )
                    );

            elapsedSeconds =
                    Math.max(
                            0,
                            plannedSeconds - remainingSeconds
                    );
        }

        if (elapsedSeconds <= 0) {

            resetTimer();
            return;
        }

        if (swingTimer != null) {
            swingTimer.stop();
        }

        timerRunning = false;

        saveCurrentSession("Stopped");

        resetTimer();

        updateAllProgress();
    }

    // =========================================================
    // SAVE SESSION
    // =========================================================

    private void saveCurrentSession(
            String status) {

        if (currentGoalId == -1 ||
                timerStartTime == null) {

            return;
        }

        String activity =
                activityCombo
                        .getSelectedItem()
                        .toString();

        DatabaseManager.saveSession(
                currentGoalId,
                activity,
                timerStartTime.toString(),
                LocalDateTime.now().toString(),
                plannedSeconds,
                elapsedSeconds,
                status
        );
    }

    // =========================================================
    // RESET TIMER
    // =========================================================

    private void resetTimer() {

        timerRunning = false;

        remainingSeconds = 0;
        elapsedSeconds = 0;
        plannedSeconds = 0;

        timerStartTime = null;
        timerEndNanos = 0L;

        if (swingTimer != null &&
                swingTimer.isRunning()) {

            swingTimer.stop();
        }

        timerLabel.setText("00:00:00");

        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    // =========================================================
    // TIMER LABEL
    // =========================================================

    private void updateTimerLabel() {

        int hours =
                remainingSeconds / 3600;

        int minutes =
                (remainingSeconds % 3600) / 60;

        int seconds =
                remainingSeconds % 60;

        timerLabel.setText(
                String.format(
                        "%02d:%02d:%02d",
                        hours,
                        minutes,
                        seconds
                )
        );
    }

    // =========================================================
    // ADD CUSTOM TOPIC
    // =========================================================

    private void addCustomTopic() {

        if (currentGoalId == -1) {

            return;
        }

        String topic =
                JOptionPane.showInputDialog(
                        this,
                        "Enter roadmap topic:"
                );

        if (topic == null ||
                topic.trim().isEmpty()) {

            return;
        }

        DatabaseManager.addRoadmapTopic(
                currentGoalId,
                topic.trim()
        );

        loadRoadmap();

        updateAllProgress();
    }

    // =========================================================
    // UPDATE ROADMAP
    // =========================================================

    private void updateRoadmapByIndex(int index, String status) {

        if (index < 0 || index >= roadmapModel.size()) return;

        String selected = roadmapModel.getElementAt(index);

        try {
            int separator = selected.indexOf("|");
            int id = Integer.parseInt(selected.substring(0, separator));

            DatabaseManager.updateRoadmapStatus(id, status);
            loadRoadmap();
            updateAllProgress();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "COULD NOT UPDATE ROADMAP."
            );
        }
    }

    private void updateSelectedRoadmap(
            String status) {

        int index =
                roadmapList.getSelectedIndex();

        if (index == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Select a topic first."
            );

            return;
        }

        String selected =
                roadmapModel.getElementAt(
                        index
                );

        try {

            int separator =
                    selected.indexOf("|");

            int id =
                    Integer.parseInt(
                            selected.substring(
                                    0,
                                    separator
                            )
                    );

            DatabaseManager
                    .updateRoadmapStatus(
                            id,
                            status
                    );

            loadRoadmap();

            updateAllProgress();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update roadmap."
            );
        }
    }

    // =========================================================
    // LOAD SAVED GOAL
    // =========================================================

    private void loadSavedGoal() {

        currentGoalId =
                DatabaseManager
                        .getLatestGoalId();

        if (currentGoalId == -1) {

            return;
        }

        String name =
                DatabaseManager
                        .getGoalName(
                                currentGoalId
                        );

        double hours =
                DatabaseManager
                        .getGoalTargetHours(
                                currentGoalId
                        );

        int remaining =
                DatabaseManager
                        .getGoalDeadlineDays(
                                currentGoalId
                        );

        int todayTarget =
                DatabaseManager
                        .getDailyTarget(
                                currentGoalId,
                                LocalDate.now()
                        );

        goalNameField.setText(
                name
        );

        targetHoursField.setText(
                String.valueOf(
                        hours
                )
        );

        deadlineField.setText(
                String.valueOf(
                        remaining
                )
        );

        dailyTargetField.setText(
                String.valueOf(
                        todayTarget
                )
        );

        goalLabel.setText(
                "Current Goal: "
                        + name
        );

        deadlineLabel.setText(
                "Remaining Days: "
                        + remaining
        );

        dailyTargetLabel.setText(
                "Today's Target: "
                        + todayTarget
                        + " min"
        );

        if (resourceGoalLabel != null) {
            resourceGoalLabel.setText(name);
        }

        /*
         * Important:
         * If old UPSC goal has no roadmap,
         * automatically create it.
         */
        if (name.toLowerCase()
                .contains("upsc") &&
                DatabaseManager
                        .getTotalRoadmapTopics(
                                currentGoalId
                        ) == 0) {

            DatabaseManager
                    .addDefaultUPSCRoadmap(
                            currentGoalId
                    );
        }

        loadRoadmap();

        updateAllProgress();
        updateAIInsightsMetrics();
    }

    // =========================================================
    // LOAD ROADMAP
    // =========================================================

    private void loadRoadmap() {

        roadmapModel.clear();

        if (currentGoalId == -1) {

            return;
        }

        String roadmap =
                DatabaseManager.getRoadmap(
                        currentGoalId
                );

        if (roadmap.isEmpty()) {

            return;
        }

        String[] lines =
                roadmap.split("\n");

        for (String line : lines) {

            if (line.trim().isEmpty()) {

                continue;
            }

            String[] parts =
                    line.split(
                            "\\|",
                            3
                    );

            if (parts.length == 3) {

                String icon =
                        parts[2].equals(
                                "Completed"
                        )
                        ? "✓ "
                        : "□ ";

                roadmapModel.addElement(
                        parts[0]
                        + "|"
                        + icon
                        + parts[1]
                        + " ["
                        + parts[2]
                        + "]"
                );
            }
        }
    }

    // =========================================================
    // UPDATE ALL
    // =========================================================

    private void updateAllProgress() {

        if (currentGoalId == -1) {

            return;
        }

        int todaySeconds =
                DatabaseManager
                        .getTodayStudySeconds(
                                currentGoalId
                        );

        int totalSeconds =
                DatabaseManager
                        .getTotalStudySeconds(
                                currentGoalId
                        );

        int target =
                DatabaseManager
                        .getDailyTarget(
                                currentGoalId,
                                LocalDate.now()
                        );

        double timeProgress =
                DatabaseManager
                        .getTimeProgress(
                                currentGoalId
                        );

        double roadmapProgress =
                DatabaseManager
                        .getRoadmapProgress(
                                currentGoalId
                        );

        double smartProgress =
                DatabaseManager
                        .getSmartGoalProgress(
                                currentGoalId
                        );

        double todayPercent = 0;

        if (target > 0) {

            todayPercent =
                    todaySeconds
                    * 100.0
                    / (target * 60);
        }

        todayPercent =
                Math.min(
                        100,
                        todayPercent
                );

        todayStudyLabel.setText(
                "Today's Study: "
                + formatSeconds(
                        todaySeconds
                )
        );

        overallStudyLabel.setText(
                "Overall Study: "
                + formatSeconds(
                        totalSeconds
                )
        );

        roadmapProgressLabel.setText(
                "Roadmap Progress: "
                + String.format(
                        "%.1f",
                        roadmapProgress
                )
                + "%"
        );

        smartProgressLabel.setText(
                "Smart Goal Progress: "
                + String.format(
                        "%.1f",
                        smartProgress
                )
                + "%"
        );

        todayProgressBar.setValue(
                (int) todayPercent
        );

        overallProgressBar.setValue(
                (int) timeProgress
        );

        roadmapProgressBar.setValue(
                (int) roadmapProgress
        );

        smartProgressBar.setValue(
                (int) smartProgress
        );

        String goal =
                DatabaseManager.getGoalName(
                        currentGoalId
                );

        dashboardGoal.setText(
                "<html><center>"
                + "Goal<br>"
                + goal
                + "</center></html>"
        );

        dashboardToday.setText(
                "<html><center>"
                + "Today<br>"
                + String.format(
                        "%.1f",
                        todayPercent
                )
                + "%</center></html>"
        );

        dashboardOverall.setText(
                "<html><center>"
                + "Overall<br>"
                + String.format(
                        "%.1f",
                        timeProgress
                )
                + "%</center></html>"
        );

        dashboardRoadmap.setText(
                "<html><center>"
                + "Roadmap<br>"
                + String.format(
                        "%.1f",
                        roadmapProgress
                )
                + "%</center></html>"
        );

        dashboardSmart.setText(
                "<html><center>"
                + "Smart<br>"
                + String.format(
                        "%.1f",
                        smartProgress
                )
                + "%</center></html>"
        );

        loadSevenDayPerformance();

        loadHistory();

        loadBenchmark();

        updateAIInsightsMetrics();

        if (chartPanel != null) {

            chartPanel.repaint();
        }
    }

    // =========================================================
    // 7 DAY PERFORMANCE
    // =========================================================

    private void loadSevenDayPerformance() {

        if (currentGoalId == -1) {

            return;
        }

        int total =
                DatabaseManager
                        .getLast7DaysStudySeconds(
                                currentGoalId
                        );

        int average =
                DatabaseManager
                        .getLast7DaysAverageStudySeconds(
                                currentGoalId
                        );

        int streak =
                DatabaseManager
                        .getCurrentStudyStreak(
                                currentGoalId
                        );

        double consistency =
                DatabaseManager
                        .getLast7DaysConsistency(
                                currentGoalId
                        );

        String top =
                DatabaseManager
                        .getMostStudiedActivityLast7Days(
                                currentGoalId
                        );

        sevenDayTotalLabel.setText(
                "<html><center>"
                + "Total<br>"
                + formatSeconds(total)
                + "</center></html>"
        );

        sevenDayAverageLabel.setText(
                "<html><center>"
                + "Average<br>"
                + formatSeconds(average)
                + "</center></html>"
        );

        streakLabel.setText(
                "<html><center>"
                + "Streak<br>"
                + streak
                + " days</center></html>"
        );

        consistencyLabel.setText(
                "<html><center>"
                + "Consistency<br>"
                + String.format(
                        "%.1f",
                        consistency
                )
                + "%</center></html>"
        );

        topActivityLabel.setText(
                "<html><center>"
                + "Top Activity<br>"
                + top
                + "</center></html>"
        );

        sevenDayArea.setText(
                DatabaseManager
                        .getLast7DaysPerformanceReport(
                                currentGoalId
                        )
        );
    }

    // =========================================================
    // HISTORY
    // =========================================================

    private void loadHistory() {

        if (currentGoalId == -1) {

            return;
        }

        String history = DatabaseManager.getActivityHistory(currentGoalId);
        if (history == null || history.trim().isEmpty()) {
            history = "NO ACTIVITY SESSIONS SAVED YET.\n\nSTART THE TIMER TO RECORD YOUR FIRST SESSION.";
        }
        historyArea.setText(history);
    }

    // =========================================================
    // BENCHMARK
    // =========================================================

    private void loadBenchmark() {

        if (benchmarkArea == null) {

            return;
        }

        if (currentGoalId == -1) {

            benchmarkArea.setText(
                    """
                    🎯 SMART BENCHMARK

                    Create a goal first.

                    After creating a goal,
                    this section will compare:

                    • Your actual progress
                    • Expected progress by timeline
                    • Roadmap progress
                    • Study time progress
                    • 7-day consistency

                    No fake world-aspirant
                    statistics are used.
                    """
            );

            benchmarkStatusLabel.setText(
                    "Waiting for goal..."
            );

            benchmarkProgressBar.setValue(
                    0
            );

            return;
        }

        double score =
                DatabaseManager
                        .getBenchmarkScore(
                                currentGoalId
                        );

        String status =
                DatabaseManager
                        .getBenchmarkStatus(
                                currentGoalId
                        );

        benchmarkStatusLabel.setText(
                "STATUS: "
                        + status
        );

        benchmarkProgressBar.setValue(
                (int)
                Math.min(
                        100,
                        score
                )
        );

        benchmarkProgressBar.setString(
                String.format(
                        "%.1f%%",
                        score
                )
        );

        benchmarkArea.setText(
                DatabaseManager
                        .getBenchmarkReport(
                                currentGoalId
                        )
        );
    }

    // =========================================================
    // ASK AI
    // =========================================================


    private void askAI() {

        if (currentGoalId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Create a goal first."
            );

            return;
        }

        String question =
                aiQuestionField
                        .getText()
                        .trim();

        if (question.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Type a question."
            );

            return;
        }

        runAIQuestion(
                question,
                askAIButton,
                aiOutputArea
        );
    }

    private void runAIQuestion(
            String question,
            JButton busyButton,
            JTextArea outputArea
    ) {

        String prompt =
                buildAIContext()
                + """

                USER QUESTION:
                %s

                Answer in a practical and easy way.

                Use this structure:
                1. What the data shows
                2. What needs attention
                3. What the user should do next

                Never invent external benchmark statistics.
                If verified external benchmark data is not supplied,
                say that it is unavailable.
                """.formatted(
                        question
                );

        runAIAsync(
                prompt,
                busyButton,
                outputArea,
                null
        );
    }

    private String buildAIContext() {

        String goal =
                DatabaseManager.getGoalName(
                        currentGoalId
                );

        double targetHours =
                DatabaseManager.getGoalTargetHours(
                        currentGoalId
                );

        int today =
                DatabaseManager.getTodayStudySeconds(
                        currentGoalId
                );

        int dailyTarget =
                DatabaseManager.getDailyTarget(
                        currentGoalId,
                        LocalDate.now()
                );

        int total =
                DatabaseManager.getTotalStudySeconds(
                        currentGoalId
                );

        double roadmap =
                DatabaseManager.getRoadmapProgress(
                        currentGoalId
                );

        double smart =
                DatabaseManager.getSmartGoalProgress(
                        currentGoalId
                );

        double benchmark =
                DatabaseManager.getBenchmarkScore(
                        currentGoalId
                );

        double consistency =
                DatabaseManager.getLast7DaysConsistency(
                        currentGoalId
                );

        int completed =
                DatabaseManager.getCompletedRoadmapTopics(
                        currentGoalId
                );

        int totalTopics =
                DatabaseManager.getTotalRoadmapTopics(
                        currentGoalId
                );

        int streak =
                DatabaseManager.getCurrentStudyStreak(
                        currentGoalId
                );

        String activityHistory =
                DatabaseManager.getActivityHistory(
                        currentGoalId
                );

        String targetActual =
                DatabaseManager.getTargetVsActualReport(
                        currentGoalId
                );

        String performance =
                DatabaseManager.getLast7DaysPerformanceReport(
                        currentGoalId
                );

        String benchmarkReport =
                DatabaseManager.getBenchmarkReport(
                        currentGoalId
                );

        String roadmapData =
                DatabaseManager.getRoadmap(
                        currentGoalId
                );

        return """
                You are the AI Coach of Smart Time Coach.

                Analyze the user's actual stored productivity data.

                GOAL:
                %s

                OVERALL TARGET:
                %.2f hours

                TODAY STUDY:
                %s

                TODAY TARGET:
                %d minutes

                TOTAL STUDY:
                %s

                ROADMAP:
                %.1f%%

                ROADMAP TOPICS:
                %d completed out of %d

                SMART GOAL PROGRESS:
                %.1f%%

                INTERNAL TIMELINE BENCHMARK SCORE:
                %.1f%%

                7-DAY CONSISTENCY:
                %.1f%%

                CURRENT STREAK:
                %d days

                ACTIVITY HISTORY:
                %s

                TARGET VS ACTUAL:
                %s

                7-DAY PERFORMANCE:
                %s

                BENCHMARK ANALYSIS:
                %s

                ROADMAP DETAILS:
                %s

                """.formatted(
                        goal,
                        targetHours,
                        formatSeconds(today),
                        dailyTarget,
                        formatSeconds(total),
                        roadmap,
                        completed,
                        totalTopics,
                        smart,
                        benchmark,
                        consistency,
                        streak,
                        activityHistory,
                        targetActual,
                        performance,
                        benchmarkReport,
                        roadmapData
                );
    }


    // =========================================================
    // AI INSIGHT METRICS
    // =========================================================

    private void updateAIInsightsMetrics() {

        if (aiInsightsArea == null) {
            return;
        }

        if (currentGoalId == -1) {

            if (goalHealthLabel != null) {
                goalHealthLabel.setText("0%");
            }

            if (goalHealthBar != null) {
                goalHealthBar.setValue(0);
            }

            if (executionLabel != null) {
                executionLabel.setText("0%");
            }

            if (executionBar != null) {
                executionBar.setValue(0);
            }

            if (insightConsistencyLabel != null) {
                insightConsistencyLabel.setText("0%");
            }

            if (insightRoadmapLabel != null) {
                insightRoadmapLabel.setText("0%");
            }

            return;
        }

        double health =
                calculateGoalHealth();

        double execution =
                getExecutionScore();

        double consistency =
                DatabaseManager.getLast7DaysConsistency(
                        currentGoalId
                );

        double roadmap =
                DatabaseManager.getRoadmapProgress(
                        currentGoalId
                );

        setMetric(
                goalHealthLabel,
                goalHealthBar,
                health
        );

        setMetric(
                executionLabel,
                executionBar,
                execution
        );

        if (insightConsistencyLabel != null) {

            insightConsistencyLabel.setText(
                    String.format(
                            "%.1f%%",
                            consistency
                    )
            );
        }

        if (insightRoadmapLabel != null) {

            insightRoadmapLabel.setText(
                    String.format(
                            "%.1f%%",
                            roadmap
                    )
            );
        }
    }

    private void setMetric(
            JLabel label,
            JProgressBar bar,
            double value
    ) {

        double safeValue =
                Math.max(
                        0,
                        Math.min(
                                100,
                                value
                        )
                );

        if (label != null) {

            label.setText(
                    String.format(
                            "%.1f%%",
                            safeValue
                    )
            );
        }

        if (bar != null) {

            bar.setValue(
                    (int) Math.round(
                            safeValue
                    )
            );
        }
    }

    private double calculateGoalHealth() {

        if (currentGoalId == -1) {
            return 0;
        }

        int dailyTarget =
                DatabaseManager.getDailyTarget(
                        currentGoalId,
                        LocalDate.now()
                );

        int todaySeconds =
                DatabaseManager.getTodayStudySeconds(
                        currentGoalId
                );

        double todayAdherence =
                dailyTarget > 0
                ? Math.min(
                        100,
                        todaySeconds * 100.0
                        / (dailyTarget * 60.0)
                )
                : 50;

        double overall =
                DatabaseManager.getTimeProgress(
                        currentGoalId
                );

        double roadmap =
                DatabaseManager.getRoadmapProgress(
                        currentGoalId
                );

        double consistency =
                DatabaseManager.getLast7DaysConsistency(
                        currentGoalId
                );

        return clampPercent(
                overall * 0.25
                + roadmap * 0.30
                + consistency * 0.25
                + todayAdherence * 0.20
        );
    }

    private double getExecutionScore() {

        if (currentGoalId == -1) {
            return 0;
        }

        String url =
                "jdbc:sqlite:smarttimecoach.db";

        String sql =
                """
                SELECT
                    COALESCE(SUM(planned_seconds), 0),
                    COALESCE(SUM(actual_seconds), 0)
                FROM sessions
                WHERE goal_id = ?
                  AND start_time >= ?
                """;

        try (
                Connection connection =
                        DriverManager.getConnection(url);
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    currentGoalId
            );

            statement.setString(
                    2,
                    LocalDateTime.now()
                            .minusDays(7)
                            .toString()
            );

            try (
                    ResultSet result =
                            statement.executeQuery()
            ) {

                if (!result.next()) {
                    return 0;
                }

                long planned =
                        result.getLong(1);

                long actual =
                        result.getLong(2);

                if (planned <= 0) {

                    return actual > 0
                            ? 100
                            : 0;
                }

                return clampPercent(
                        actual * 100.0
                        / planned
                );
            }

        } catch (Exception e) {

            return 0;
        }
    }

    private double clampPercent(
            double value
    ) {

        return Math.max(
                0,
                Math.min(
                        100,
                        value
                )
        );
    }

    // =========================================================
    // GRAPH
    // =========================================================

    private class StudyChartPanel
            extends JPanel {

        public StudyChartPanel() {

            setPreferredSize(
                    new Dimension(
                            800,
                            450
                    )
            );

            setBackground(
                    Color.WHITE
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            int left = 70;
            int right = 30;
            int top = 35;
            int bottom = 55;

            int chartWidth = Math.max(1, width - left - right);
            int chartHeight = Math.max(1, height - top - bottom);

            if (currentGoalId == -1) {
                drawEmptyChart(g2, width, height,
                        "NO GOAL CREATED YET",
                        "Create a goal and start your first timer session.");
                g2.dispose();
                return;
            }

            int[] values = new int[7];
            String[] days = new String[7];
            int max = 0;
            int total = 0;

            for (int i = 0; i < 7; i++) {
                LocalDate date = LocalDate.now().minusDays(6 - i);
                values[i] = Math.max(0, DatabaseManager.getStudySecondsForDate(currentGoalId, date));
                days[i] = date.getDayOfWeek().toString().substring(0, 3);
                max = Math.max(max, values[i]);
                total += values[i];
            }

            if (total <= 0) {
                drawEmptyChart(g2, width, height,
                        "NO STUDY DATA FOR THE LAST 7 DAYS",
                        "Use the Timer tab to record study sessions. Your weekly chart will appear here.");
                g2.dispose();
                return;
            }

            g2.setColor(new Color(232, 237, 242));
            for (int i = 0; i <= 5; i++) {
                int y = top + chartHeight - (i * chartHeight / 5);
                g2.drawLine(left, y, left + chartWidth, y);
            }

            // Keep the Y-axis scale meaningful for very small sessions.
            // A single 1-second session must NOT become a full-height bar.
            int scaleMax = Math.max(15 * 60, max);

            int gap = Math.max(10, chartWidth / 80);
            int barWidth = Math.max(20, (chartWidth - gap * 8) / 7);

            for (int i = 0; i < 7; i++) {
                int barHeight = (int) Math.round(values[i] * 1.0 / scaleMax * chartHeight);
                int x = left + gap + i * (barWidth + gap);
                int y = top + chartHeight - barHeight;

                g2.setColor(new Color(47, 133, 102));
                if (barHeight > 0) {
                    g2.fillRoundRect(x, y, barWidth, Math.max(2, barHeight), 8, 8);
                }

                g2.setColor(new Color(18, 52, 86));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.drawString(days[i], x + Math.max(0, barWidth / 2 - 10), top + chartHeight + 24);

                String value = formatShortTime(values[i]);
                int valueWidth = g2.getFontMetrics().stringWidth(value);
                int labelY;
                if (barHeight > 0) {
                    labelY = Math.max(top + 14, y - 6);
                } else {
                    labelY = top + chartHeight - 8;
                }
                g2.drawString(value, x + (barWidth - valueWidth) / 2, labelY);
            }

            g2.setColor(new Color(70, 85, 100));
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(left, top, left, top + chartHeight);
            g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight);

            g2.dispose();
        }

        private void drawEmptyChart(Graphics2D g2, int width, int height,
                                    String title, String message) {
            g2.setColor(new Color(246, 249, 252));
            g2.fillRoundRect(30, 25, Math.max(100, width - 60), Math.max(100, height - 55),
                    18, 18);

            g2.setColor(new Color(18, 52, 86));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            int tw = g2.getFontMetrics().stringWidth(title);
            g2.drawString(title, Math.max(20, (width - tw) / 2), height / 2 - 8);

            g2.setColor(new Color(90, 105, 120));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            int mw = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, Math.max(20, (width - mw) / 2), height / 2 + 20);
        }
    }

    // =========================================================
    // SHORT TIME
    // =========================================================

    private String formatShortTime(
            int seconds) {

        int hours =
                seconds / 3600;

        int minutes =
                (seconds % 3600)
                        / 60;

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }

        if (minutes > 0) {
            return minutes + "m";
        }

        if (seconds > 0) {
            return seconds + "s";
        }

        return "0m";
    }

    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatSeconds(
            int seconds) {

        int hours =
                seconds / 3600;

        int minutes =
                (seconds % 3600)
                        / 60;

        int sec =
                seconds % 60;

        return String.format(
                "%02dh %02dm %02ds",
                hours,
                minutes,
                sec
        );
    }


    // =========================================================
    // PROFESSIONAL UI HELPERS
    // =========================================================

    private JLabel sectionTitle(String icon, String title) {
        JLabel label = new JLabel(
                icon + "  " + title.toUpperCase(),
                SwingConstants.LEFT
        );
        label.setFont(new Font("Segoe UI", Font.BOLD, 21));
        label.setForeground(new Color(15, 39, 66));
        label.setBorder(new EmptyBorder(6, 6, 10, 6));
        return label;
    }

    private JButton premiumButton(String text, String icon) {
        JButton button = new JButton(icon + "  " + text.toUpperCase());
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
        button.setCursor(new java.awt.Cursor(
                java.awt.Cursor.HAND_CURSOR
        ));
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        return button;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setPreferredSize(new Dimension(300, 42));
        field.setBorder(
                javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createLineBorder(
                                new Color(203, 213, 225)
                        ),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );
    }

    private void styleProgressBar(JProgressBar bar) {
        bar.setStringPainted(true);
        bar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bar.setForeground(new Color(37, 99, 235));
        bar.setBackground(new Color(226, 232, 240));
        bar.setBorderPainted(false);
    }

    // =========================================================
    // LIGHTWEIGHT VECTOR ICONS
    // =========================================================

    private static class SimpleUiIcon implements Icon {

        private final String type;
        private final Color color;
        private final int width;
        private final int height;

        SimpleUiIcon(String type, Color color, int width, int height) {
            this.type = type;
            this.color = color;
            this.width = width;
            this.height = height;
        }

        @Override
        public int getIconWidth() { return width; }

        @Override
        public int getIconHeight() { return height; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = x + width / 2;
            int cy = y + height / 2;

            switch (type) {
                case "home":
                    int[] roof = {x + 2, cy, cx, y + 3, x + width - 2, cy};
                    g2.drawPolyline(roof, new int[]{y + 3, y + 3, y + 3}, 0);
                    g2.drawLine(x + 4, cy, x + 4, y + height - 3);
                    g2.drawLine(x + width - 4, cy, x + width - 4, y + height - 3);
                    g2.drawLine(x + 4, y + height - 3, x + width - 4, y + height - 3);
                    g2.drawLine(cx - 2, y + height - 3, cx - 2, y + height - 8);
                    g2.drawLine(cx + 2, y + height - 3, cx + 2, y + height - 8);
                    break;
                case "goal":
                    g2.drawOval(x + 3, y + 3, width - 6, height - 6);
                    g2.drawOval(x + 6, y + 6, width - 12, height - 12);
                    g2.fillOval(cx - 2, cy - 2, 4, 4);
                    break;
                case "timer":
                    g2.drawOval(x + 3, y + 4, width - 6, height - 6);
                    g2.drawLine(cx, cy, cx, y + 7);
                    g2.drawLine(cx, cy, cx + 4, cy + 3);
                    g2.drawLine(cx - 3, y + 2, cx + 3, y + 2);
                    break;
                case "progress":
                case "benchmark":
                    g2.drawLine(x + 3, y + height - 3, x + 3, y + 4);
                    g2.drawLine(x + 3, y + height - 3, x + width - 2, y + height - 3);
                    g2.drawLine(x + 6, y + height - 6, x + 9, y + 9);
                    g2.drawLine(x + 9, y + 9, x + 12, y + 11);
                    g2.drawLine(x + 12, y + 11, x + width - 3, y + 5);
                    break;
                case "roadmap":
                    g2.drawRoundRect(x + 3, y + 3, width - 6, height - 6, 3, 3);
                    g2.drawLine(cx, y + 4, cx, y + height - 4);
                    g2.drawLine(x + 4, cy, x + width - 4, cy);
                    break;
                case "calendar":
                    g2.drawRoundRect(x + 3, y + 4, width - 6, height - 6, 2, 2);
                    g2.drawLine(x + 3, y + 8, x + width - 3, y + 8);
                    g2.drawLine(x + 7, y + 2, x + 7, y + 6);
                    g2.drawLine(x + width - 7, y + 2, x + width - 7, y + 6);
                    g2.fillRect(x + 6, y + 11, 2, 2);
                    g2.fillRect(x + 11, y + 11, 2, 2);
                    break;
                case "history":
                    g2.drawRoundRect(x + 4, y + 3, width - 6, height - 6, 2, 2);
                    g2.drawLine(x + 7, y + 7, x + width - 5, y + 7);
                    g2.drawLine(x + 7, y + 10, x + width - 5, y + 10);
                    g2.drawLine(x + 7, y + 13, x + width - 8, y + 13);
                    break;
                case "ai":
                    g2.drawOval(x + 4, y + 4, width - 8, height - 8);
                    g2.drawLine(cx, y + 1, cx, y + 4);
                    g2.drawLine(cx, y + height - 4, cx, y + height - 1);
                    g2.drawLine(x + 1, cy, x + 4, cy);
                    g2.drawLine(x + width - 4, cy, x + width - 1, cy);
                    g2.drawLine(cx - 3, cy, cx + 3, cy);
                    g2.drawLine(cx, cy - 3, cx, cy + 3);
                    break;
                case "refresh":
                    g2.drawArc(x + 3, y + 3, width - 6, height - 6, 35, 285);
                    g2.drawLine(x + width - 3, y + 5, x + width - 3, y + 10);
                    g2.drawLine(x + width - 3, y + 5, x + width - 8, y + 5);
                    break;
                case "save":
                    g2.drawRoundRect(x + 3, y + 2, width - 6, height - 4, 2, 2);
                    g2.drawRect(x + 6, y + 3, width - 12, 6);
                    g2.drawRect(x + 6, y + 11, width - 12, 5);
                    break;
                case "start":
                    int[] px = {x + 5, x + 5, x + width - 4};
                    int[] py = {y + 3, y + height - 3, cy};
                    g2.fillPolygon(px, py, 3);
                    break;
                case "pause":
                    g2.fillRoundRect(x + 4, y + 3, 4, height - 6, 2, 2);
                    g2.fillRoundRect(x + width - 8, y + 3, 4, height - 6, 2, 2);
                    break;
                case "stop":
                    g2.fillRoundRect(x + 4, y + 4, width - 8, height - 8, 2, 2);
                    break;
                case "add":
                    g2.drawLine(cx, y + 3, cx, y + height - 3);
                    g2.drawLine(x + 3, cy, x + width - 3, cy);
                    break;
                case "complete":
                    g2.drawOval(x + 2, y + 2, width - 4, height - 4);
                    g2.drawLine(x + 5, cy, cx - 1, y + height - 5);
                    g2.drawLine(cx - 1, y + height - 5, x + width - 4, y + 5);
                    break;
                case "pending":
                    g2.drawRect(x + 3, y + 3, width - 6, height - 6);
                    break;
                case "analyze":
                    g2.drawOval(x + 3, y + 3, 10, 10);
                    g2.drawLine(x + 11, y + 12, x + width - 3, y + height - 3);
                    break;
                case "insights":
                    g2.drawOval(x + 4, y + 3, width - 8, height - 7);
                    g2.drawLine(cx - 5, cy - 2, cx + 5, cy - 2);
                    g2.drawLine(cx - 5, cy + 2, cx + 5, cy + 2);
                    g2.drawLine(cx, y + 4, cx, y + height - 4);
                    break;
                case "status":
                    g2.fillOval(x + 4, y + 4, width - 8, height - 8);
                    break;
                default:
                    g2.drawRoundRect(x + 3, y + 3, width - 6, height - 6, 3, 3);
                    break;
            }
            g2.dispose();
        }
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
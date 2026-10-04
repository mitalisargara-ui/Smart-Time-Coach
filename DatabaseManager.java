import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlite:smarttimecoach.db";

    // =========================================================
    // DATABASE CONNECTION
    // =========================================================

    private static Connection connect()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }
    // =========================================================
    // CREATE TABLES
    // =========================================================

    public static void createTables() {

        String goalsTable =
                """
                CREATE TABLE IF NOT EXISTS goals (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    goal_name TEXT NOT NULL,
                    target_minutes INTEGER NOT NULL,
                    deadline_date TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """;

        String activitiesTable =
                """
                CREATE TABLE IF NOT EXISTS activities (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    activity_name TEXT UNIQUE NOT NULL
                )
                """;

        String sessionsTable =
                """
                CREATE TABLE IF NOT EXISTS sessions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    goal_id INTEGER NOT NULL,
                    activity_id INTEGER NOT NULL,
                    start_time TEXT NOT NULL,
                    end_time TEXT NOT NULL,
                    planned_seconds INTEGER NOT NULL,
                    actual_seconds INTEGER NOT NULL,
                    status TEXT NOT NULL,
                    FOREIGN KEY(goal_id)
                        REFERENCES goals(id),
                    FOREIGN KEY(activity_id)
                        REFERENCES activities(id)
                )
                """;

        String dailyTargetsTable =
                """
                CREATE TABLE IF NOT EXISTS daily_targets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    goal_id INTEGER NOT NULL,
                    target_date TEXT NOT NULL,
                    target_minutes INTEGER NOT NULL,
                    created_at TEXT NOT NULL,
                    UNIQUE(goal_id, target_date),
                    FOREIGN KEY(goal_id)
                        REFERENCES goals(id)
                )
                """;

        String roadmapTable =
                """
                CREATE TABLE IF NOT EXISTS roadmap (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    goal_id INTEGER NOT NULL,
                    topic_name TEXT NOT NULL,
                    status TEXT NOT NULL
                        DEFAULT 'Pending',
                    created_at TEXT NOT NULL,
                    FOREIGN KEY(goal_id)
                        REFERENCES goals(id)
                )
                """;

        String benchmarkTable =
                """
                CREATE TABLE IF NOT EXISTS benchmarks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    benchmark_name TEXT NOT NULL,
                    benchmark_value REAL NOT NULL,
                    unit TEXT NOT NULL,
                    source TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """;

        try (Connection con = connect();
             Statement st = con.createStatement()) {

            st.execute(goalsTable);
            st.execute(activitiesTable);
            st.execute(sessionsTable);
            st.execute(dailyTargetsTable);
            st.execute(roadmapTable);
            st.execute(benchmarkTable);

            insertDefaultActivities(con);

        } catch (SQLException e) {

            System.out.println(
                    "Database creation error: "
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // DEFAULT ACTIVITIES
    // =========================================================

    private static void insertDefaultActivities(
            Connection con)
            throws SQLException {

        String sql =
                """
                INSERT OR IGNORE INTO activities
                (activity_name)
                VALUES(?)
                """;

        String[] activities = {

                "Study",
                "Reading",
                "Coding",
                "Work",
                "Exercise",
                "Research",
                "Project",
                "Meeting",
                "Other",

                "History",
                "Polity",
                "Geography",
                "Economy",
                "CSAT",
                "Current Affairs",
                "Revision",
                "PYQs",
                "Mock Test"
        };

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            for (String activity : activities) {

                ps.setString(
                        1,
                        activity
                );

                ps.executeUpdate();
            }
        }
    }

    // =========================================================
    // SAVE GOAL
    // =========================================================

    public static int saveGoal(
            String goalName,
            int targetMinutes,
            int deadlineDays) {

        LocalDate deadline =
                LocalDate.now()
                        .plusDays(deadlineDays);

        String sql =
                """
                INSERT INTO goals
                (goal_name,
                 target_minutes,
                 deadline_date,
                 created_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(
                    1,
                    goalName
            );

            ps.setInt(
                    2,
                    targetMinutes
            );

            ps.setString(
                    3,
                    deadline.toString()
            );

            ps.setString(
                    4,
                    LocalDateTime.now()
                            .toString()
            );

            ps.executeUpdate();

            try (ResultSet rs =
                         ps.getGeneratedKeys()) {

                if (rs.next()) {

                    int goalId =
                            rs.getInt(1);

                    /*
                     * If goal looks like UPSC,
                     * automatically create roadmap.
                     */
                    if (goalName
                            .toLowerCase()
                            .contains("upsc")) {

                        addDefaultUPSCRoadmap(
                                goalId
                        );
                    }

                    return goalId;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Save goal error: "
                    + e.getMessage()
            );
        }

        return -1;
    }

    // =========================================================
    // LATEST GOAL
    // =========================================================

    public static int getLatestGoalId() {

        String sql =
                """
                SELECT id
                FROM goals
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {

                return rs.getInt(
                        "id"
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return -1;
    }

    // =========================================================
    // GOAL NAME
    // =========================================================

    public static String getGoalName(
            int goalId) {

        String sql =
                """
                SELECT goal_name
                FROM goals
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getString(
                            "goal_name"
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return "";
    }

    // =========================================================
    // TARGET HOURS
    // =========================================================

    public static double getGoalTargetHours(
            int goalId) {

        String sql =
                """
                SELECT target_minutes
                FROM goals
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(
                            "target_minutes"
                    ) / 60.0;
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // REMAINING DAYS
    // =========================================================

    public static int getGoalDeadlineDays(
            int goalId) {

        String sql =
                """
                SELECT deadline_date
                FROM goals
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    LocalDate deadline =
                            LocalDate.parse(
                                    rs.getString(
                                            "deadline_date"
                                    )
                            );

                    long days =
                            ChronoUnit.DAYS.between(
                                    LocalDate.now(),
                                    deadline
                            );

                    return (int)
                            Math.max(
                                    0,
                                    days
                            );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // GOAL CREATED DATE
    // =========================================================

    public static LocalDate getGoalCreatedDate(
            int goalId) {

        String sql =
                """
                SELECT created_at
                FROM goals
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    String value =
                            rs.getString(
                                    "created_at"
                            );

                    return LocalDateTime
                            .parse(value)
                            .toLocalDate();
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return LocalDate.now();
    }

    // =========================================================
    // GOAL DEADLINE DATE
    // =========================================================

    public static LocalDate getGoalDeadlineDate(
            int goalId) {

        String sql =
                """
                SELECT deadline_date
                FROM goals
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return LocalDate.parse(
                            rs.getString(
                                    "deadline_date"
                            )
                    );
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return LocalDate.now();
    }

    // =========================================================
    // DAILY TARGET
    // =========================================================

    public static void saveDailyTarget(
            int goalId,
            LocalDate date,
            int targetMinutes) {

        String sql =
                """
                INSERT INTO daily_targets
                (goal_id,
                 target_date,
                 target_minutes,
                 created_at)
                VALUES (?, ?, ?, ?)

                ON CONFLICT(goal_id, target_date)
                DO UPDATE SET
                target_minutes =
                excluded.target_minutes
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            ps.setString(
                    2,
                    date.toString()
            );

            ps.setInt(
                    3,
                    targetMinutes
            );

            ps.setString(
                    4,
                    LocalDateTime.now()
                            .toString()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            System.out.println(
                    "Daily target error: "
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // GET DAILY TARGET
    // =========================================================

    public static int getDailyTarget(
            int goalId,
            LocalDate date) {

        String sql =
                """
                SELECT target_minutes
                FROM daily_targets
                WHERE goal_id=?
                AND target_date=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            ps.setString(
                    2,
                    date.toString()
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(
                            "target_minutes"
                    );
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // ACTIVITY ID
    // =========================================================

    private static int getActivityId(
            Connection con,
            String activityName)
            throws SQLException {

        String sql =
                """
                SELECT id
                FROM activities
                WHERE activity_name=?
                """;

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    activityName
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(
                            "id"
                    );
                }
            }
        }

        return -1;
    }

    // =========================================================
    // SAVE SESSION
    // =========================================================

    public static void saveSession(
            int goalId,
            String activityName,
            String startTime,
            String endTime,
            int plannedSeconds,
            int actualSeconds,
            String status) {

        String sql =
                """
                INSERT INTO sessions
                (goal_id,
                 activity_id,
                 start_time,
                 end_time,
                 planned_seconds,
                 actual_seconds,
                 status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = connect()) {

            int activityId =
                    getActivityId(
                            con,
                            activityName
                    );

            if (activityId == -1) {

                return;
            }

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setInt(
                        1,
                        goalId
                );

                ps.setInt(
                        2,
                        activityId
                );

                ps.setString(
                        3,
                        startTime
                );

                ps.setString(
                        4,
                        endTime
                );

                ps.setInt(
                        5,
                        plannedSeconds
                );

                ps.setInt(
                        6,
                        actualSeconds
                );

                ps.setString(
                        7,
                        status
                );

                ps.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println(
                    "Session save error: "
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // TOTAL STUDY
    // =========================================================

    public static int getTotalStudySeconds(
            int goalId) {

        String sql =
                """
                SELECT COALESCE(
                    SUM(actual_seconds),0
                )
                FROM sessions
                WHERE goal_id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // TODAY STUDY
    // =========================================================

    public static int getTodayStudySeconds(
            int goalId) {

        return getStudySecondsForDate(
                goalId,
                LocalDate.now()
        );
    }

    // =========================================================
    // STUDY FOR DATE
    // =========================================================

    public static int getStudySecondsForDate(
            int goalId,
            LocalDate date) {

        String sql =
                """
                SELECT COALESCE(
                    SUM(actual_seconds),0
                )
                FROM sessions
                WHERE goal_id=?
                AND date(start_time)=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            ps.setString(
                    2,
                    date.toString()
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // ACTIVITY HISTORY
    // =========================================================

    public static String getActivityHistory(
            int goalId) {

        StringBuilder result =
                new StringBuilder();

        String sql =
                """
                SELECT
                    a.activity_name,
                    SUM(s.actual_seconds)
                FROM sessions s
                JOIN activities a
                ON s.activity_id=a.id
                WHERE s.goal_id=?
                GROUP BY a.activity_name
                ORDER BY
                    SUM(s.actual_seconds)
                    DESC
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    result.append(
                            rs.getString(1)
                    )
                    .append(" = ")
                    .append(
                            formatSeconds(
                                    rs.getInt(2)
                            )
                    )
                    .append("\n");
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return result.toString();
    }

    // =========================================================
    // LAST 7 DAYS STUDY
    // =========================================================

    public static int getLast7DaysStudySeconds(
            int goalId) {

        int total = 0;

        for (int i = 0; i < 7; i++) {

            total +=
                    getStudySecondsForDate(
                            goalId,
                            LocalDate.now()
                                    .minusDays(i)
                    );
        }

        return total;
    }

    // =========================================================
    // LAST 7 DAYS AVERAGE
    // =========================================================

    public static int getLast7DaysAverageStudySeconds(
            int goalId) {

        return getLast7DaysStudySeconds(
                goalId
        ) / 7;
    }

    // =========================================================
    // TARGET ACHIEVED
    // =========================================================

    public static int getLast7DaysTargetAchieved(
            int goalId) {

        int achieved = 0;

        for (int i = 0; i < 7; i++) {

            LocalDate date =
                    LocalDate.now()
                            .minusDays(i);

            int target =
                    getDailyTarget(
                            goalId,
                            date
                    );

            int actual =
                    getStudySecondsForDate(
                            goalId,
                            date
                    );

            if (target > 0 &&
                    actual >= target * 60) {

                achieved++;
            }
        }

        return achieved;
    }

    // =========================================================
    // CONSISTENCY
    // =========================================================

    public static double getLast7DaysConsistency(
            int goalId) {

        int daysWithTarget = 0;
        int achievedDays = 0;

        for (int i = 0; i < 7; i++) {

            LocalDate date =
                    LocalDate.now()
                            .minusDays(i);

            int target =
                    getDailyTarget(
                            goalId,
                            date
                    );

            if (target > 0) {

                daysWithTarget++;

                int actual =
                        getStudySecondsForDate(
                                goalId,
                                date
                        );

                if (actual >= target * 60) {

                    achievedDays++;
                }
            }
        }

        if (daysWithTarget == 0) {

            return 0;
        }

        return achievedDays
                * 100.0
                / daysWithTarget;
    }

    // =========================================================
    // CURRENT STREAK
    // =========================================================

    public static int getCurrentStudyStreak(
            int goalId) {

        int streak = 0;

        for (int i = 0; i < 365; i++) {

            LocalDate date =
                    LocalDate.now()
                            .minusDays(i);

            if (getStudySecondsForDate(
                    goalId,
                    date
            ) > 0) {

                streak++;

            } else {

                break;
            }
        }

        return streak;
    }

    // =========================================================
    // TOP ACTIVITY
    // =========================================================

    public static String getMostStudiedActivityLast7Days(
            int goalId) {

        String sql =
                """
                SELECT a.activity_name
                FROM sessions s
                JOIN activities a
                ON s.activity_id=a.id
                WHERE s.goal_id=?
                AND date(s.start_time)
                    >= date('now','-6 day')
                GROUP BY a.activity_name
                ORDER BY
                    SUM(s.actual_seconds)
                    DESC
                LIMIT 1
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getString(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return "None";
    }

    // =========================================================
    // 7 DAY BREAKDOWN
    // =========================================================

    public static String getLast7DaysBreakdown(
            int goalId) {

        StringBuilder result =
                new StringBuilder();

        for (int i = 6; i >= 0; i--) {

            LocalDate date =
                    LocalDate.now()
                            .minusDays(i);

            int actual =
                    getStudySecondsForDate(
                            goalId,
                            date
                    );

            int target =
                    getDailyTarget(
                            goalId,
                            date
                    );

            result.append(
                    date
            )
            .append(" | Study: ")
            .append(
                    formatSeconds(
                            actual
                    )
            )
            .append(" | Target: ")
            .append(target)
            .append(" min\n");
        }

        return result.toString();
    }

    // =========================================================
    // TARGET VS ACTUAL
    // =========================================================

    public static String getTargetVsActualReport(
            int goalId) {

        StringBuilder result =
                new StringBuilder();

        for (int i = 6; i >= 0; i--) {

            LocalDate date =
                    LocalDate.now()
                            .minusDays(i);

            int target =
                    getDailyTarget(
                            goalId,
                            date
                    );

            int actual =
                    getStudySecondsForDate(
                            goalId,
                            date
                    );

            double achievement = 0;

            if (target > 0) {

                achievement =
                        actual
                        * 100.0
                        / (target * 60);
            }

            result.append(
                    date
            )
            .append(" | Target: ")
            .append(target)
            .append(" min")
            .append(" | Actual: ")
            .append(
                    formatSeconds(
                            actual
                    )
            )
            .append(" | Achievement: ")
            .append(
                    String.format(
                            "%.1f",
                            achievement
                    )
            )
            .append("%\n");
        }

        return result.toString();
    }

    // =========================================================
    // ROADMAP ADD
    // =========================================================

    public static void addRoadmapTopic(
            int goalId,
            String topicName) {

        String checkSql =
                """
                SELECT COUNT(*)
                FROM roadmap
                WHERE goal_id=?
                AND topic_name=?
                """;

        String insertSql =
                """
                INSERT INTO roadmap
                (goal_id,
                 topic_name,
                 status,
                 created_at)
                VALUES (?, ?, 'Pending', ?)
                """;

        try (Connection con = connect()) {

            try (PreparedStatement check =
                         con.prepareStatement(
                                 checkSql
                         )) {

                check.setInt(
                        1,
                        goalId
                );

                check.setString(
                        2,
                        topicName
                );

                try (ResultSet rs =
                             check.executeQuery()) {

                    if (rs.next() &&
                            rs.getInt(1) > 0) {

                        return;
                    }
                }
            }

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 insertSql
                         )) {

                ps.setInt(
                        1,
                        goalId
                );

                ps.setString(
                        2,
                        topicName
                );

                ps.setString(
                        3,
                        LocalDateTime.now()
                                .toString()
                );

                ps.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println(
                    "Roadmap error: "
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // UPSC ROADMAP
    // =========================================================

    public static void addDefaultUPSCRoadmap(
            int goalId) {

        String[] topics = {

                "Ancient History",
                "Medieval History",
                "Modern History",

                "Indian Polity",
                "Fundamental Rights",
                "Parliament",

                "Indian Geography",
                "World Geography",

                "Indian Economy",

                "Environment",
                "General Science",

                "Current Affairs",

                "CSAT Basics",
                "CSAT Quantitative Aptitude",
                "CSAT Reasoning",
                "CSAT Comprehension",

                "Previous Year Questions",
                "Revision",
                "Mock Tests"
        };

        for (String topic : topics) {

            addRoadmapTopic(
                    goalId,
                    topic
            );
        }
    }

    // =========================================================
    // GET ROADMAP
    // =========================================================

    public static String getRoadmap(
            int goalId) {

        StringBuilder result =
                new StringBuilder();

        String sql =
                """
                SELECT id,
                       topic_name,
                       status
                FROM roadmap
                WHERE goal_id=?
                ORDER BY id
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {

                    result.append(
                            rs.getInt("id")
                    )
                    .append("|")
                    .append(
                            rs.getString(
                                    "topic_name"
                            )
                    )
                    .append("|")
                    .append(
                            rs.getString(
                                    "status"
                            )
                    )
                    .append("\n");
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return result.toString();
    }

    // =========================================================
    // UPDATE ROADMAP
    // =========================================================

    public static void updateRoadmapStatus(
            int roadmapId,
            String status) {

        String sql =
                """
                UPDATE roadmap
                SET status=?
                WHERE id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(
                    1,
                    status
            );

            ps.setInt(
                    2,
                    roadmapId
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =========================================================
    // TOTAL ROADMAP
    // =========================================================

    public static int getTotalRoadmapTopics(
            int goalId) {

        String sql =
                """
                SELECT COUNT(*)
                FROM roadmap
                WHERE goal_id=?
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // COMPLETED ROADMAP
    // =========================================================

    public static int getCompletedRoadmapTopics(
            int goalId) {

        String sql =
                """
                SELECT COUNT(*)
                FROM roadmap
                WHERE goal_id=?
                AND status='Completed'
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return 0;
    }

    // =========================================================
    // ROADMAP PROGRESS
    // =========================================================

    public static double getRoadmapProgress(
            int goalId) {

        int total =
                getTotalRoadmapTopics(
                        goalId
                );

        int completed =
                getCompletedRoadmapTopics(
                        goalId
                );

        if (total == 0) {

            return 0;
        }

        return completed
                * 100.0
                / total;
    }

    // =========================================================
    // TIME PROGRESS
    // =========================================================

    public static double getTimeProgress(
            int goalId) {

        double targetHours =
                getGoalTargetHours(
                        goalId
                );

        if (targetHours <= 0) {

            return 0;
        }

        double targetSeconds =
                targetHours * 3600;

        double actualSeconds =
                getTotalStudySeconds(
                        goalId
                );

        return Math.min(
                100,
                actualSeconds
                        * 100.0
                        / targetSeconds
        );
    }

    // =========================================================
    // ACTIVITY COVERAGE
    // =========================================================

    public static double getActivityCoverage(
            int goalId) {

        String sql =
                """
                SELECT COUNT(*)
                FROM activities a
                WHERE EXISTS (
                    SELECT 1
                    FROM sessions s
                    WHERE s.activity_id=a.id
                    AND s.goal_id=?
                )
                """;

        int used = 0;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    goalId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {

                    used = rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        int available = 18;

        return Math.min(
                100,
                used
                        * 100.0
                        / available
        );
    }

    // =========================================================
    // SMART GOAL PROGRESS
    // =========================================================

    public static double getSmartGoalProgress(
            int goalId) {

        double time =
                getTimeProgress(
                        goalId
                );

        double roadmap =
                getRoadmapProgress(
                        goalId
                );

        double consistency =
                getLast7DaysConsistency(
                        goalId
                );

        double activity =
                getActivityCoverage(
                        goalId
                );

        return
                time * 0.35
                +
                roadmap * 0.35
                +
                consistency * 0.20
                +
                activity * 0.10;
    }

    // =========================================================
    // EXPECTED TIMELINE PROGRESS
    // =========================================================

    public static double getExpectedTimelineProgress(
            int goalId) {

        LocalDate start =
                getGoalCreatedDate(
                        goalId
                );

        LocalDate deadline =
                getGoalDeadlineDate(
                        goalId
                );

        long totalDays =
                ChronoUnit.DAYS.between(
                        start,
                        deadline
                );

        long elapsedDays =
                ChronoUnit.DAYS.between(
                        start,
                        LocalDate.now()
                );

        if (totalDays <= 0) {

            return 100;
        }

        if (elapsedDays <= 0) {

            return 0;
        }

        return Math.min(
                100,
                elapsedDays
                        * 100.0
                        / totalDays
        );
    }

    // =========================================================
    // ROADMAP VS EXPECTED
    // =========================================================

    public static double getRoadmapBenchmark(
            int goalId) {

        double actual =
                getRoadmapProgress(
                        goalId
                );

        double expected =
                getExpectedTimelineProgress(
                        goalId
                );

        if (expected <= 0) {

            return actual;
        }

        return Math.min(
                100,
                actual
                        * 100.0
                        / expected
        );
    }

    // =========================================================
    // TIME VS EXPECTED
    // =========================================================

    public static double getTimeBenchmark(
            int goalId) {

        double actual =
                getTimeProgress(
                        goalId
                );

        double expected =
                getExpectedTimelineProgress(
                        goalId
                );

        if (expected <= 0) {

            return actual;
        }

        return Math.min(
                100,
                actual
                        * 100.0
                        / expected
        );
    }

    // =========================================================
    // CONSISTENCY BENCHMARK
    // =========================================================

    public static double getConsistencyBenchmark(
            int goalId) {

        return getLast7DaysConsistency(
                goalId
        );
    }

    // =========================================================
    // OVERALL BENCHMARK SCORE
    // =========================================================

    public static double getBenchmarkScore(
            int goalId) {

        double time =
                getTimeBenchmark(
                        goalId
                );

        double roadmap =
                getRoadmapBenchmark(
                        goalId
                );

        double consistency =
                getConsistencyBenchmark(
                        goalId
                );

        return
                time * 0.40
                +
                roadmap * 0.40
                +
                consistency * 0.20;
    }

    // =========================================================
    // BENCHMARK STATUS
    // =========================================================

    public static String getBenchmarkStatus(
            int goalId) {

        double score =
                getBenchmarkScore(
                        goalId
                );

        if (score >= 100) {

            return "ON TRACK / AHEAD";

        } else if (score >= 80) {

            return "NEAR TARGET";

        } else if (score >= 60) {

            return "NEEDS IMPROVEMENT";

        } else {

            return "BEHIND TARGET";
        }
    }

    // =========================================================
    // BENCHMARK REPORT
    // =========================================================

    public static String getBenchmarkReport(
            int goalId) {

        String goal =
                getGoalName(
                        goalId
                );

        int remaining =
                getGoalDeadlineDays(
                        goalId
                );

        double expected =
                getExpectedTimelineProgress(
                        goalId
                );

        double time =
                getTimeProgress(
                        goalId
                );

        double roadmap =
                getRoadmapProgress(
                        goalId
                );

        double timeBenchmark =
                getTimeBenchmark(
                        goalId
                );

        double roadmapBenchmark =
                getRoadmapBenchmark(
                        goalId
                );

        double consistency =
                getConsistencyBenchmark(
                        goalId
                );

        double score =
                getBenchmarkScore(
                        goalId
                );

        int completed =
                getCompletedRoadmapTopics(
                        goalId
                );

        int total =
                getTotalRoadmapTopics(
                        goalId
                );

        String status =
                getBenchmarkStatus(
                        goalId
                );

        StringBuilder report =
                new StringBuilder();

        report.append(
                "SMART BENCHMARK ANALYSIS\n"
        );

        report.append(
                "================================\n\n"
        );

        report.append(
                "Goal: "
        )
        .append(goal)
        .append("\n");

        report.append(
                "Remaining Days: "
        )
        .append(remaining)
        .append("\n\n");

        report.append(
                "EXPECTED BY TIMELINE\n"
        );

        report.append(
                "Expected Progress: "
        )
        .append(
                formatPercent(
                        expected
                )
        )
        .append("\n\n");

        report.append(
                "YOUR ACTUAL PROGRESS\n"
        );

        report.append(
                "Time Progress: "
        )
        .append(
                formatPercent(
                        time
                )
        )
        .append("\n");

        report.append(
                "Roadmap Progress: "
        )
        .append(
                formatPercent(
                        roadmap
                )
        )
        .append("\n");

        report.append(
                "Roadmap Topics: "
        )
        .append(completed)
        .append(" / ")
        .append(total)
        .append("\n");

        report.append(
                "7-Day Consistency: "
        )
        .append(
                formatPercent(
                        consistency
                )
        )
        .append("\n\n");

        report.append(
                "BENCHMARK INDEX\n"
        );

        report.append(
                "Time vs Timeline: "
        )
        .append(
                formatPercent(
                        timeBenchmark
                )
        )
        .append("\n");

        report.append(
                "Roadmap vs Timeline: "
        )
        .append(
                formatPercent(
                        roadmapBenchmark
                )
        )
        .append("\n");

        report.append(
                "Overall Benchmark: "
        )
        .append(
                formatPercent(
                        score
                )
        )
        .append("\n\n");

        report.append(
                "STATUS: "
        )
        .append(status)
        .append("\n\n");

        if (roadmapBenchmark < 80) {

            report.append(
                    "ACTION: Your roadmap completion "
                    + "is behind the expected timeline. "
                    + "Focus on pending roadmap topics."
            );

        } else if (timeBenchmark < 80) {

            report.append(
                    "ACTION: Your recorded study time "
                    + "is behind the expected timeline. "
                    + "Increase consistent study sessions."
            );

        } else if (consistency < 70) {

            report.append(
                    "ACTION: Your study consistency "
                    + "needs improvement. Set a daily "
                    + "target and try to achieve it "
                    + "regularly."
            );

        } else {

            report.append(
                    "ACTION: Your recorded progress "
                    + "is aligned with your goal timeline. "
                    + "Continue following your roadmap."
            );
        }

        report.append(
                "\n\nNote: This benchmark compares "
                + "your actual progress with your own "
                + "goal timeline. It does not claim to "
                + "represent world aspirants."
        );

        return report.toString();
    }

    // =========================================================
    // VERIFIED EXTERNAL BENCHMARK
    // =========================================================

    public static boolean hasBenchmark() {

        String sql =
                """
                SELECT COUNT(*)
                FROM benchmarks
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {

                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return false;
    }

    // =========================================================
    // BENCHMARK INFO
    // =========================================================

    public static String getBenchmarkInfo() {

        String sql =
                """
                SELECT benchmark_name,
                       benchmark_value,
                       unit,
                       source
                FROM benchmarks
                ORDER BY id DESC
                LIMIT 1
                """;

        try (Connection con = connect();
             PreparedStatement ps =
                     con.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            if (rs.next()) {

                return
                        rs.getString(
                                "benchmark_name"
                        )
                        + "\nValue: "
                        + rs.getDouble(
                                "benchmark_value"
                        )
                        + " "
                        + rs.getString(
                                "unit"
                        )
                        + "\nSource: "
                        + rs.getString(
                                "source"
                        );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return
                "No verified external benchmark "
                + "configured.";
    }

    // =========================================================
    // 7 DAY REPORT
    // =========================================================

    public static String getLast7DaysPerformanceReport(
            int goalId) {

        int total =
                getLast7DaysStudySeconds(
                        goalId
                );

        int average =
                getLast7DaysAverageStudySeconds(
                        goalId
                );

        int streak =
                getCurrentStudyStreak(
                        goalId
                );

        double consistency =
                getLast7DaysConsistency(
                        goalId
                );

        String top =
                getMostStudiedActivityLast7Days(
                        goalId
                );

        return
                "7-DAY PERFORMANCE\n\n"
                + "Total Study: "
                + formatSeconds(total)
                + "\n"
                + "Daily Average: "
                + formatSeconds(average)
                + "\n"
                + "Current Streak: "
                + streak
                + " days\n"
                + "Target Consistency: "
                + formatPercent(
                        consistency
                )
                + "\n"
                + "Top Activity: "
                + top
                + "\n\n"
                + getLast7DaysBreakdown(
                        goalId
                );
    }

    // =========================================================
    // DATABASE TEST
    // =========================================================

    public static boolean testConnection() {

        try (Connection con = connect()) {

            return con != null &&
                    !con.isClosed();

        } catch (SQLException e) {

            return false;
        }
    }

    // =========================================================
    // FORMAT PERCENT
    // =========================================================

    private static String formatPercent(
            double value) {

        return String.format(
                "%.1f%%",
                value
        );
    }

    // =========================================================
    // FORMAT TIME
    // =========================================================

    private static String formatSeconds(
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
}

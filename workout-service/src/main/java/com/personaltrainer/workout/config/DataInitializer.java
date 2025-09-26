package com.personaltrainer.workout.config;

import com.personaltrainer.workout.entity.Exercise;
import com.personaltrainer.workout.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample exercises
        if (exerciseRepository.count() == 0) {
            initializeExercises();
        }
    }

    private void initializeExercises() {
        // Chest exercises
        exerciseRepository.save(new Exercise(
            "Push-ups", 
            "A bodyweight exercise that targets chest, shoulders, and triceps",
            "Chest", 
            "Bodyweight", 
            2,
            "Start in plank position, lower body until chest nearly touches floor, push back up"
        ));

        exerciseRepository.save(new Exercise(
            "Bench Press", 
            "Classic chest exercise using barbell",
            "Chest", 
            "Barbell", 
            3,
            "Lie on bench, lower barbell to chest, press up until arms are extended"
        ));

        // Back exercises
        exerciseRepository.save(new Exercise(
            "Pull-ups", 
            "Upper body exercise targeting back and biceps",
            "Back", 
            "Pull-up Bar", 
            4,
            "Hang from bar, pull body up until chin clears bar, lower with control"
        ));

        exerciseRepository.save(new Exercise(
            "Bent-over Rows", 
            "Back exercise using dumbbells or barbell",
            "Back", 
            "Dumbbell", 
            3,
            "Bend at hips, keep back straight, pull weights to lower ribs, squeeze shoulder blades"
        ));

        // Leg exercises
        exerciseRepository.save(new Exercise(
            "Squats", 
            "Compound exercise targeting quadriceps, glutes, and hamstrings",
            "Legs", 
            "Bodyweight", 
            2,
            "Stand with feet shoulder-width apart, lower hips back and down, drive through heels to stand"
        ));

        exerciseRepository.save(new Exercise(
            "Deadlifts", 
            "Full-body compound exercise",
            "Legs", 
            "Barbell", 
            5,
            "Stand with barbell over mid-foot, bend at hips and knees, lift bar by driving hips forward"
        ));

        // Shoulder exercises
        exerciseRepository.save(new Exercise(
            "Shoulder Press", 
            "Overhead pressing movement for shoulders",
            "Shoulders", 
            "Dumbbell", 
            3,
            "Stand or sit, press weights overhead until arms are extended, lower with control"
        ));

        // Arm exercises
        exerciseRepository.save(new Exercise(
            "Bicep Curls", 
            "Isolation exercise for biceps",
            "Arms", 
            "Dumbbell", 
            2,
            "Stand with weights at sides, curl weights up by flexing biceps, lower slowly"
        ));

        exerciseRepository.save(new Exercise(
            "Tricep Dips", 
            "Bodyweight exercise for triceps",
            "Arms", 
            "Bodyweight", 
            3,
            "Sit on edge of chair/bench, lower body by bending elbows, push back up"
        ));

        // Core exercises
        exerciseRepository.save(new Exercise(
            "Plank", 
            "Isometric core strengthening exercise",
            "Core", 
            "Bodyweight", 
            2,
            "Hold push-up position with forearms on ground, keep body straight"
        ));

        System.out.println("Sample exercises initialized successfully!");
    }
}
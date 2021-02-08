package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 01,00,0), "Праздник", 500),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY,01,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2021, Month.JANUARY, 01,20,0), "Ужин", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.DECEMBER,31,10,0), "Завтрак", 300),
                new UserMeal(LocalDateTime.of(2020, Month.DECEMBER, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.DECEMBER,31,22,0), "Поздний ужин", 1000)
        );
//        filteredByCycles(mealList, LocalTime.of(7,0), LocalTime.of(12,0), 2000);
//        System.out.println(filteredByCycles(mealList, LocalTime.of(07,0), LocalTime.of(12,0), 2000));
//        System.out.println(filteredByStreams(mealList, LocalTime.of(00,0), LocalTime.of(10,0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = new TreeMap<>();

        for (UserMeal userMeal : mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            int calories = userMeal.getCalories();
            if (sumCaloriesPerDay.containsKey(localDate)) {
                calories  = calories + sumCaloriesPerDay.get(localDate);
            }
            sumCaloriesPerDay.put(localDate, calories);
        }

        List<UserMealWithExcess> filteredMeal = new ArrayList<>();

        for (UserMeal meal : mealList) {
            boolean excess = sumCaloriesPerDay.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
            LocalTime time = meal.getDateTime().toLocalTime();
            if ((time.isAfter(startTime) || time.equals(startTime)) && (time.isBefore(endTime) || time.equals(endTime))) {
                filteredMeal.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
            }
        }
        return filteredMeal;
    }

/*
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = new TreeMap<>();

        Map<LocalDate, UserMealWithExcess> filteredMeal = new TreeMap<>();

        for (UserMeal userMeal : mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            Integer calories = userMeal.getCalories();
            if (sumCaloriesPerDay.containsKey(localDate)) {
                calories  = calories + sumCaloriesPerDay.get(localDate);
            }
            sumCaloriesPerDay.put(localDate, calories);
            boolean excess = (sumCaloriesPerDay.get(localDate) > caloriesPerDay) ? true : false;
            if (excess == true) {
                UserMealWithExcess userMealWithExcess = filteredMeal.get(localDate);
                if (userMealWithExcess != null) {
                    filteredMeal.put(localDate, new UserMealWithExcess(userMealWithExcess.getDateTime(), userMealWithExcess.getDescription(), userMeal.getCalories(), excess));
                }
            }
            LocalTime time = userMeal.getDateTime().toLocalTime();
            if ((time.isAfter(startTime) || time.equals(startTime)) && (time.isBefore(endTime) || time.equals(endTime))) {
                filteredMeal.put(localDate, new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess));
            }
        }
        return new ArrayList<>(filteredMeal.values());
    }
*/


    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = new TreeMap<>();
        mealList.stream()
                .collect(Collectors.groupingBy(userMeal -> userMeal.getDateTime().toLocalDate()))
                .forEach((localDate, listUserMealByDate) -> {
                    sumCaloriesPerDay.put(localDate, listUserMealByDate.stream().mapToInt(UserMeal::getCalories).sum());
                });

        List<UserMealWithExcess> filteredMealsList = new ArrayList<>();
                mealList.stream()
                .filter(userMeal -> (userMeal.getDateTime().toLocalTime().isAfter(startTime) || userMeal.getDateTime().toLocalTime().equals(startTime))
                        && (userMeal.getDateTime().toLocalTime().isBefore(endTime) || userMeal.getDateTime().toLocalTime().equals(endTime)))
                .forEach(userMeal -> {
                    filteredMealsList.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(),
                            sumCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay));
                });

        return filteredMealsList;
    }
}

/*
        Реализовать метод `UserMealsUtil.filteredByCycles` через циклы (`forEach`):
        -  должны возвращаться только записи между `startTime` и `endTime`
        -  поле `UserMealWithExcess.excess` должно показывать,
        превышает ли сумма калорий за весь день значение `caloriesPerDay`

        Т.е `UserMealWithExcess` - это запись одной еды, но поле `excess` будет одинаково для всех записей за этот день.

        - Проверьте результат выполнения ДЗ (можно проверить логику в http://topjava.herokuapp.com , список еды)
        - Оцените Time complexity алгоритма. Если она больше O(N), например O(N*N) или N*log(N), сделайте O(N).

        Реализовать метод `UserMealsUtil.filteredByStreams` через Java 8 Stream API.
*/

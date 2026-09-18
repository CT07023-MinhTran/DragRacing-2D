package com.dragracing.game.data;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CarDatabase {
    public static List<Car> createDefaultCars() {
        List<Car> cars = new ArrayList<>();

        // Tier 1: accessible sports cars and entry-level performance cars.
        cars.add(car("car_toyota_86gt", "Toyota 86GT", "Tier 1: Sport", 0,
                "#1565C0", 228, 1270, 0.98, 0.35, 7500, 800, 6500, 7200,
                new double[]{3.63, 2.19, 1.54, 1.21, 1.00, 0.77}, 4.10,
                Car.BodyType.TUNER, "toyota_86gt"));
        cars.add(car("car_toyota_gr86", "Toyota GR86", "Tier 1: Sport", 28000,
                "#C62828", 232, 1275, 1.02, 0.35, 7500, 800, 6600, 7300,
                new double[]{3.63, 2.19, 1.54, 1.21, 1.00, 0.77}, 4.10,
                Car.BodyType.TUNER, "toyota_gr86"));
        cars.add(car("car_honda_s2000", "Honda S2000", "Tier 1: Sport", 25000,
                "#EEEEEE", 240, 1250, 1.05, 0.32, 9000, 900, 8000, 8800,
                new double[]{3.13, 2.04, 1.48, 1.16, 0.97, 0.81}, 4.10,
                Car.BodyType.TUNER, "honda_s2000"));
        cars.add(car("car_alfa_romeo_4c", "Alfa Romeo 4C", "Tier 1: Sport", 18000,
                "#E53935", 237, 895, 1.12, 0.15, 6500, 900, 5200, 6100,
                new double[]{3.54, 2.24, 1.52, 1.16, 0.91, 0.74}, 3.63,
                Car.BodyType.SUPER, "alfa_romeo_4c"));
        cars.add(car("car_alpine_a110", "Alpine A110", "Tier 1: Sport", 22000,
                "#42A5F5", 300, 1103, 1.14, 0.18, 7000, 900, 5600, 6500,
                new double[]{3.13, 2.10, 1.48, 1.16, 0.94, 0.78}, 3.73,
                Car.BodyType.TUNER, "alpine_a110"));
        cars.add(car("car_lotus_exige_s", "Lotus Exige S", "Tier 1: Sport", 55000,
                "#FFD600", 345, 1176, 1.20, 0.30, 7200, 1000, 6200, 7000,
                new double[]{3.08, 1.96, 1.43, 1.10, 0.89, 0.71}, 3.77,
                Car.BodyType.SUPER, "lotus_exige_s"));

        // Tier 2: modern sports coupes and grand tourers.
        cars.add(car("car_bmw_m3", "BMW M3 (E92)", "Tier 2: Sport", 42000,
                "#EEEEEE", 414, 1605, 1.08, 0.30, 8400, 800, 7600, 8200,
                new double[]{4.06, 2.40, 1.58, 1.19, 1.00, 0.87, 0.73}, 3.85,
                Car.BodyType.TUNER, "bmw_m3"));
        cars.add(car("car_bmw_m4", "BMW M4", "Tier 2: Sport", 45000,
                "#0D47A1", 503, 1725, 1.10, 0.14, 7200, 850, 6000, 6800,
                new double[]{4.11, 2.32, 1.54, 1.18, 1.00, 0.85}, 3.15,
                Car.BodyType.TUNER, "bmw_m4"));
        cars.add(car("car_lotus_emira", "Lotus Emira", "Tier 2: Sport", 75000,
                "#1B5E20", 400, 1405, 1.18, 0.28, 7000, 900, 6000, 6800,
                new double[]{3.30, 2.08, 1.48, 1.16, 0.94, 0.78}, 3.73,
                Car.BodyType.SUPER, "lotus_emira"));
        cars.add(car("car_aston_martin_vantage", "Aston Martin V8 Vantage", "Tier 2: GT", 65000,
                "#1B5E20", 528, 1530, 1.08, 0.20, 7000, 850, 5700, 6600,
                new double[]{3.23, 2.08, 1.43, 1.10, 0.90, 0.72}, 3.73,
                Car.BodyType.SUPER, "aston_martin_v8_vantage"));
        cars.add(car("car_mercedes_amg_gt_s", "Mercedes AMG GT S", "Tier 2: GT", 90000,
                "#212121", 523, 1645, 1.09, 0.14, 7000, 850, 5800, 6700,
                new double[]{3.40, 2.22, 1.52, 1.22, 1.00, 0.81}, 3.67,
                Car.BodyType.SUPER, "mercedes_amg_gt_s"));
        cars.add(car("car_bmw_m6", "BMW M6 Coupe", "Tier 2: GT", 85000,
                "#1A237E", 560, 1850, 1.06, 0.16, 7200, 800, 6200, 7000,
                new double[]{4.81, 2.59, 1.70, 1.28, 1.00, 0.84, 0.67}, 3.31,
                Car.BodyType.CLASSIC, "bmw_m6"));
        cars.add(car("car_lexus_lc500", "Lexus LC500", "Tier 2: GT", 95000,
                "#4E342E", 471, 1970, 1.00, 0.22, 7100, 700, 5700, 6600,
                new double[]{3.30, 1.90, 1.42, 1.00, 0.71, 0.58}, 3.13,
                Car.BodyType.CLASSIC, "lexus_lc500"));

        // Tier 3: high-performance road cars.
        cars.add(car("car_toyota_gr_supra", "Toyota GR Supra RZ", "Tier 3: Performance", 55000,
                "#0D47A1", 382, 1570, 1.10, 0.15, 7000, 800, 5700, 6600,
                new double[]{3.59, 2.19, 1.54, 1.21, 1.00, 0.82}, 3.15,
                Car.BodyType.TUNER, "toyota_gr_supra_rz"));
        cars.add(car("car_honda_civic_type_r", "Honda Civic Type R", "Tier 3: Performance", 46000,
                "#E53935", 315, 1430, 1.08, 0.32, 7000, 900, 6000, 6700,
                new double[]{3.27, 2.13, 1.52, 1.15, 0.95, 0.79}, 4.11,
                Car.BodyType.TUNER, "honda_civic_type_r"));
        cars.add(car("car_porsche_cayman_s", "Porsche 718 Cayman S", "Tier 3: Performance", 72000,
                "#1565C0", 350, 1385, 1.14, 0.15, 7500, 800, 6200, 7000,
                new double[]{3.91, 2.29, 1.58, 1.18, 0.94, 0.79}, 3.62,
                Car.BodyType.SUPER, "porsche_718_cayman_s"));
        cars.add(car("car_porsche_cayman_gts", "Porsche 718 Cayman GTS", "Tier 3: Performance", 80000,
                "#EEEEEE", 394, 1450, 1.16, 0.15, 7800, 800, 6500, 7300,
                new double[]{3.75, 2.38, 1.72, 1.34, 1.08, 0.88}, 3.89,
                Car.BodyType.SUPER, "porsche_718_cayman_gts"));
        cars.add(car("car_porsche_cayman_gt4", "Porsche 718 Cayman GT4", "Tier 3: Performance", 105000,
                "#FBC02D", 414, 1420, 1.22, 0.30, 8000, 900, 7000, 7800,
                new double[]{3.31, 1.95, 1.41, 1.13, 0.95, 0.81}, 3.89,
                Car.BodyType.SUPER, "porsche_718_cayman_gt4"));
        cars.add(car("car_honda_nsx", "Honda NSX", "Tier 3: Supercar", 160000,
                "#B71C1C", 573, 1725, 1.18, 0.12, 7500, 1000, 6200, 7100,
                new double[]{3.35, 2.10, 1.55, 1.18, 0.93, 0.75}, 3.58,
                Car.BodyType.SUPER, "honda_nsx"));

        // Tier 4: exotic supercars.
        cars.add(car("car_bmw_m4_dtm", "BMW M4 DTM Champion", "Tier 4: Track", 200000,
                "#EEEEEE", 493, 1510, 1.28, 0.12, 7600, 900, 6500, 7400,
                new double[]{4.11, 2.32, 1.54, 1.18, 1.00, 0.85, 0.73}, 3.15,
                Car.BodyType.SUPER, "bmw_m4_dtm_champion_edition"));
        cars.add(car("car_mercedes_amg_gt_r", "Mercedes AMG GT R", "Tier 4: Supercar", 160000,
                "#43A047", 577, 1630, 1.25, 0.14, 7000, 900, 6000, 6800,
                new double[]{3.08, 2.19, 1.63, 1.29, 1.03, 0.84, 0.70}, 3.88,
                Car.BodyType.SUPER, "mercedes_amg_gt_r"));
        cars.add(car("car_porsche_911_carrera_gts", "Porsche 911 Carrera GTS", "Tier 4: Supercar", 175000,
                "#1565C0", 473, 1515, 1.20, 0.15, 7500, 850, 6300, 7100,
                new double[]{3.91, 2.29, 1.65, 1.30, 1.08, 0.88}, 3.44,
                Car.BodyType.SUPER, "porsche_911_carrera_gts"));
        cars.add(car("car_porsche_911_gt3", "Porsche 911 GT3", "Tier 4: Supercar", 225000,
                "#1976D2", 502, 1435, 1.24, 0.15, 9000, 1000, 7600, 8500,
                new double[]{3.75, 2.38, 1.72, 1.34, 1.08, 0.88}, 3.89,
                Car.BodyType.SUPER, "car_porsche_911_gt3"));
        cars.add(car("car_audi_r8_v10_coupe", "Audi R8 V10 Plus", "Tier 4: Supercar", 210000,
                "#455A64", 602, 1595, 1.18, 0.14, 8700, 900, 7000, 8000,
                new double[]{3.13, 2.10, 1.52, 1.18, 0.95, 0.80}, 3.37,
                Car.BodyType.SUPER, "audi_r8_v10_plus"));
        cars.add(car("car_nissan_gtr_nismo", "Nissan GTR Nismo", "Tier 4: Supercar", 220000,
                "#212121", 600, 1720, 1.22, 0.12, 7000, 900, 6000, 6800,
                new double[]{3.83, 2.36, 1.69, 1.31, 1.00, 0.83}, 3.70,
                Car.BodyType.TUNER, "car_nissan_gtr_nismo"));

        // Tier 5: flagship supercars.
        cars.add(car("car_ferrari_488_gtb", "Ferrari 488 GTB", "Tier 5: Supercar", 280000,
                "#C62828", 661, 1475, 1.28, 0.12, 8000, 1000, 6800, 7600,
                new double[]{3.08, 2.19, 1.63, 1.29, 1.03, 0.84}, 3.46,
                Car.BodyType.SUPER, "car_ferrari_488_gtb"));
        cars.add(car("car_lamborghini_huracan", "Lamborghini Huracan", "Tier 5: Supercar", 260000,
                "#FFD600", 631, 1422, 1.30, 0.14, 8500, 1000, 7200, 8000,
                new double[]{3.91, 2.44, 1.81, 1.46, 1.18, 0.97}, 3.40,
                Car.BodyType.SUPER, "lamborghini_huracan"));

        cars.sort(Comparator.comparingInt(CarDatabase::getTierNumber));
        return cars;
    }

    private static Car car(String id, String name, String carClass, int price, String color,
                           double horsepower, double weight, double grip, double shiftTime,
                           int maxRpm, int idleRpm, int shiftMin, int shiftMax,
                           double[] gearRatios, double finalDrive, Car.BodyType bodyType,
                           String imageResourceName) {
        return new Car(id, name, carClass, price, Color.parseColor(color), horsepower, weight,
                grip, shiftTime, maxRpm, idleRpm, shiftMin, shiftMax, gearRatios, finalDrive,
                bodyType, imageResourceName);
    }

    private static int getTierNumber(Car car) {
        String carClass = car.getCarClass();
        int tierStart = carClass.indexOf("Tier ") + 5;
        int tierEnd = carClass.indexOf(':', tierStart);
        if (tierStart < 5 || tierEnd < 0) return Integer.MAX_VALUE;
        return Integer.parseInt(carClass.substring(tierStart, tierEnd));
    }
}

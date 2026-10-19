package coursehub.Lab6.time;

import java.time.LocalDate;

public class StudyCalendar {
    public static void run() {
        LocalDate start = LocalDate.of(2024, 2, 1); 
        System.out.println("старт курса: " + start);

        LocalDate module2Start = start.plusDays(30);
        LocalDate module3Start = start.plusDays(60);
        
        System.out.println("начало 2 модуля: " + module2Start);
        System.out.println("начало 3 модуля: " + module3Start);

        LocalDate lastDay = start.plusMonths(3);
        System.out.println("последний учебный день: " + lastDay);

        System.out.println("последний день в високосном году? " + lastDay.isLeapYear());

        System.out.println("исходная дата старта не изменилась: " + start);
    }

    public static void main(String[] args) {run();}
}
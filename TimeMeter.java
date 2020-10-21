package hwMultitrading;

public class TimeMeter {
    private String title;                       //Описание выполняемого метода (операции, вычисления)
    private long workingTime;                   //Время, сколько длилось выполнение
    private long startTime;                     //Время начала отсчета
    private long stopTime;                      //Время конца отсчета

    /**
     * Запишет время начала
     */
    public void timeStart() {
        startTime = System.currentTimeMillis();
    }

    /**
     * Запишет время завершения
     */
    public void timeStop() {
        stopTime = System.currentTimeMillis();
    }

    /**
     * Вернет разницу между отсетками старт и стоп
     *
     * @param title - название
     * @return - сообщение
     */
    public String timeInfo(String title) {
        this.title = title;
        workingTime = stopTime - startTime;
        String timeMessage = (title + ". Длительность выполнения: " + workingTime + " мс.");
        return timeMessage;
    }

    /**
     * Вернет время работы
     *
     * @return
     */
    public long getWorkingTime() {
        return workingTime;
    }

}

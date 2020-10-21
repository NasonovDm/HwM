package hwMultitrading;

public class MainMultithreadingTask {
    static final int size = 10000000;                   //Размеры массива
    static final short threadCount = 2;                 //Количество потоков

    public static void main(String[] args) {

        //Наполняем массив значениями (1 поток)
        TimeMeter timeMeter_1 = new TimeMeter();
        timeMeter_1.timeStart();
        float[] arrS = new float[size];                 //Массив для 1 потока
        arrS = fillArray(arrS);                         //Заполняем массив значениями
        timeMeter_1.timeStop();
        System.out.println(timeMeter_1.timeInfo("Создан первый массив заполненный единицами. Размер массива: " + size));


        //Вычисляем значения по формуле (1 поток)
        TimeMeter timeMeter_2 = new TimeMeter();
        timeMeter_2.timeStart();
        arrS = calculatingValuesInArray(arrS);          //Вычисляем значения
        timeMeter_2.timeStop();
        System.out.println(timeMeter_2.timeInfo("\nПроизведены вычисления элементов массива по формуле в 1 поток"));


        //Наполняем второй массив значениями (1 поток)
        TimeMeter timeMeter_3 = new TimeMeter();
        timeMeter_3.timeStart();
        float[] arrM = new float[size];                 //Массив для многопоточности одномерный
        arrM = fillArray(arrM);                         //Заполняем массив значениями
        timeMeter_3.timeStop();
        System.out.println(timeMeter_3.timeInfo("\nСоздан второй массив заполненный единицами. Размер массива: " + size));


        //Делим массив на части
        TimeMeter timeMeter_4 = new TimeMeter();
        timeMeter_4.timeStart();
        float[][] arrMulti;                                     //Массив для многопоточности двумерный
        arrMulti = segmentationArray(arrM, threadCount);        //Делим массив на части
        timeMeter_4.timeStop();
        System.out.println(timeMeter_4.timeInfo("\nВторой массив поделен на части (по количеству потоков: " + threadCount + ")"));


        //Формируем массив с потоками и запускаем его
        TimeMeter timeMeter_5 = new TimeMeter();
        timeMeter_5.timeStart();
        Thread[] arrThreads = new Thread[threadCount];                                                  //Массив в который поместим потоки
        for (int i = 0; i < arrThreads.length; i++) {
            CalculatingValuesInArrayMulti cvia = new CalculatingValuesInArrayMulti(arrMulti[i]);        //Класс в котором описана логика
            arrThreads[i] = new Thread(cvia);                                                           //Создаем поток
            arrThreads[i].start();                                                                      //Запускаем
            try {
                arrThreads[i].join();                                                                   //Главный поток должен дождаться завершения всех потоков
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            arrMulti[i] = cvia.getArr();
        }
        timeMeter_5.timeStop();
        System.out.println(timeMeter_5.timeInfo("\nПроизведены вычисления элементов массива по формуле (по количеству потоков: " + threadCount + ")"));


        //Собираем массив обратно
        TimeMeter timeMeter_6 = new TimeMeter();
        timeMeter_6.timeStart();
        arrM = gluingArray(arrMulti, size);                     //Склеиваем массив
        timeMeter_6.timeStop();
        System.out.println(timeMeter_6.timeInfo("\nВторой массив собран из частей (по количеству потоков: " + threadCount + ")"));


        //Сравниваем работу потоков и выводим информацию о них
        long oneThreadWorkingTime = timeMeter_2.getWorkingTime() ;
        long multiThreadWorkingTime = timeMeter_4.getWorkingTime() + timeMeter_5.getWorkingTime() + timeMeter_6.getWorkingTime();
        System.out.println("\nДля одного потока вычисления длились: " + oneThreadWorkingTime + " мс.");
        System.out.println("Для мультипотока вычисления длились: " + multiThreadWorkingTime + " мс.");
        float difference = (float)oneThreadWorkingTime / (float)multiThreadWorkingTime;
        System.out.println("Мультипоток отработал в " + difference + " раз быстрее чем один поток.");

    }

    /**
     * Метод который заполнит массив значениями (1 поток)
     * @param arr - на вход подается пусой массив
     * @return - возвращается массив со значениями
     */
    private static float[] fillArray(float[] arr) {
        int count = 1;
        for (int i = 0; i < arr.length ; i++) {
            arr[i] = count;
        }
        return arr;
    }

    /**
     * Расчет элементов массива по формуле (1 поток)
     * @param arr - на вход подается массив
     * @return - возвращается массив со значениями
     */
    public static float[] calculatingValuesInArray(float[] arr) {
        for (int i = 0; i < arr.length ; i++) {
            arr[i] = (float)((arr[i] + 14) * Math.sin(0.2f + i / 5) * Math.cos(0.2f + i / 5) * Math.cos(0.4f + i / 2));
        }
        return arr;
    }

    /**
     * Метод делит массив на несколько подмассивов
     * @param arr - массив, который будет разделен на части
     * @param count - на сколько частей будет разделен массив
     * @return - Двумерный массив, состоящий из подмассивов
     */
    private static float[][] segmentationArray(float[] arr, short count){
        int startIndex = 0;
        int numberOfElementsInArray = arr.length / count;
        int residueOfElements = arr.length % count;
        float[][] array = new float[count][];

        for (int i = 0; i < count - 1; i++) {
            array[i] = new float[numberOfElementsInArray];
            System.arraycopy(arr, startIndex, array[i], 0, numberOfElementsInArray);
            startIndex += numberOfElementsInArray;
        }
        array[count-1] = new float[numberOfElementsInArray + residueOfElements];
        System.arraycopy(arr, startIndex, array[count-1], 0, array[count-1].length);
        return array;
    }

    /**
     * Метод собирает массив из несколько подмассивов
     * @param arr - двумерный массив, из которого необходимо собрать один массив
     * @param size - длина массива
     * @return - одномерный массив, собраный из подмассивов
     */
    private static float[] gluingArray(float[][] arr, int size){
        float[] array = new float[size];
        int startIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, array, startIndex, arr[i].length);
            startIndex += arr[i].length;
        }
        return array;
    }

}


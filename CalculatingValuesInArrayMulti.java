package hwMultitrading;

public class CalculatingValuesInArrayMulti implements Runnable {

        private float[] arr;

        CalculatingValuesInArrayMulti(float[] arr){
            this.arr = arr;
        }
        @Override
        public void run() {
            MainMultithreadingTask.calculatingValuesInArray(arr);
        }

        public float[] getArr() {
            return arr;
        }
    }

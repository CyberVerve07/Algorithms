package algorithm.searching;

import java.util.Arrays;

public class TernarySearch {

    public static int ternarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid1 = left + (right - left) / 3;
            int mid2 = right - (right - left) / 3;

            if (arr[mid1] == target) {
                return mid1;
            }
            if (arr[mid2] == target) {
                return mid2;
            }

            if (target < arr[mid1]) {
                right = mid1 - 1;
            } else if (target > arr[mid2]) {
                left = mid2 + 1;
            } else {
                left = mid1 + 1;
                right = mid2 - 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {3, 7, 12, 18, 25, 31, 42, 56, 64, 79};
        int target = 42;

        int index = ternarySearch(arr, target);
        System.out.println("Array: " + Arrays.toString(arr));
        System.out.println("Target: " + target);
        System.out.println(index >= 0 ? "Found at index: " + index : "Not found");
    }
}

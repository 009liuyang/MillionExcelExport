package com.ly.study.suanfa;

/**
 * @author yang.liu
 * @describe
 * @date 2020-04-08
 */
public class suanfaTest {

    public static void main(String[] args) {
        jiaoji();
    }

    /**
     * 交集
     */
    private static void jiaoji(){
        int[] a = {2,5,6,8,9};
        int[] b = {1,4,5,7,9};

        int i = 0;
        int j = 0;
        while (i<a.length && j<b.length){
            if(a[i] > b[j]){
                j ++;
            }else if(a[i] < b[j]){
                i ++;
            }else {
                System.out.println(a[i]);
                i ++;
                j ++;
            }
        }
    }

    /**
     * 3个线程交替打印
     */

}

package com.mypack;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;
public class Main {
    @Test
    public void test1(){
        System.out.println("1");
        //mock creation
        List mockedList = mock(List.class);

        //using mock object
        mockedList.add("one");
        mockedList.clear();

        //verification
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }
    @Test
    public void test2(){
        LinkedList mockedList = mock(LinkedList.class);

        //stubbing
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException());

        //following prints "first"
        System.out.println(mockedList.get(0));

        //following throws runtime exception
//        System.out.println(mockedList.get(1));

        //following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));

        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
        //If your code doesn't care what get(0) returns, then it should not be stubbed.
        verify(mockedList).get(0);
    }
    @Test
    public void test3(){
        LinkedList mockedList = mock(LinkedList.class);
        //stubbing using built-in anyInt() argument matcher
        when(mockedList.get(anyInt())).thenReturn("element");
        mockedList.add("element");
        //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
        when(mockedList.contains(argThat(new CustomMatcher()))).thenReturn(true);
//        when(mockedList.contains(argThat(argument -> argument.toString().length() > 5))).thenReturn(true);

        //following prints "element"
        System.out.println(mockedList.get(999));

        //you can also verify using an argument matcher
        verify(mockedList).get(anyInt());

//        argument matchers can also be written as Java 8 Lambdas
        verify(mockedList).add(argThat(new CustomMatcher()));
    }
    @Test
    public void test4(){
        UserService mock = mock(UserService.class);
        mock.add(1,"2","third argument");
        verify(mock).add(anyInt(), anyString(), eq("third argument"));
    }
    @Test
    public void test5(){
        LinkedList mockedList = mock(LinkedList.class);
        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        //following two verifications work exactly the same - times(1) is used by default
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        //exact number of invocations verification
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        //verification using never(). never() is an alias to times(0)
        verify(mockedList, never()).add("never happened");

        //verification using atLeast()/atMost()
        verify(mockedList, atMostOnce()).add("once");
        verify(mockedList, atLeastOnce()).add("three times");
        verify(mockedList, atLeast(2)).add("three times");
        verify(mockedList, atMost(5)).add("three times");

    }
    @Test
    public void test6(){
        List singleMock = mock(List.class);

        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //create an inOrder verifier for a single mock
        InOrder inOrder = inOrder(singleMock);

        //following will make sure that add is first called with "was added first", then with "was added second"
        inOrder.verify(singleMock).add("was added first");
        inOrder.verify(singleMock).add("was added second");

        // B. Multiple mocks that must be used in a particular order
        List firstMock = mock(List.class);
        List secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder2 = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before secondMock
        inOrder2.verify(firstMock).add("was called first");
        inOrder2.verify(secondMock).add("was called second");




        singleMock.add("one");

        //ordinary verification
        verify(singleMock).add("one");

        //verify that method was never called on a mock
        verify(singleMock, never()).add("two");
    }
    @Test
    public void test7(){

        LinkedList mockedList = mock(LinkedList.class);
        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");
        verify(mockedList).add("two");

        //following verification will fail

        //就是看看是不是已经没有对应
        verifyNoMoreInteractions(mockedList);
    }
}

class UserService{
    public void add(int a,String b,String c){
        System.out.println("真实方法被调用了");
    }
}

class CustomMatcher implements ArgumentMatcher<String> {

    public String age;
    public String name;
    public String interesting;
    public String copyOnWriteArrayList;
    public String time;
    public String start;
    @Override
    public boolean matches(String argument) {
        if (argument != null) {
            // 在这里编写你的匹配逻辑，例如检查字符串长度是否大于 5
            return argument.length() > 5;
        }
        return false;
    }
}

**1. Explain why recursive implementation of QuickSort will require O(log n) of additional space.**  
The Quicksort algorithm randomly selects a pivot element; partitions the array around the pivot; and recursively calls the algorithm on the two subarrays that are formed before and after the pivot element. Given a subarray of n elements, the expected value of the index of the pivot element is n/2 in each recursive call, and thus the expected value of the depth of recursion (i.e. the height of function call stack to reach the base condition that the length of the subarray is 1) is log_base2(n). This means at most an O(log n) additional space, which is implicit in the function call stack, is required for the algorithm.  
&nbsp;  
**2. Explain the design pattern used in Java and .NET io stream/reader APIs.**  
The `InputStream` and `Reader` implementations in Java use the Decorator pattern, where the methods of one class are delegated to the methods of another class of the same type, and the calling type adds additional behavior around (i.e. "decorates") the delegated methods. For example, the constructor of the concrete class `BufferedInputStream`, takes an instance of the same interface type `InputStream`, and adds additional buffering behavior around the methods defined in `InputSTream`.  
&nbsp;  
Also, the Adapter pattern is used to wrap an `InputStream` type as a `Reader` type. For example, `InputStreamReader` can take an instance of `InputStream` as a constructor parameter, and wraps the passed `InputStream` instance in a way that the data read from the stream is implicitly converted to a character stream.  
&nbsp;  
**3. Coding Test**  
Test: Create an Iterator filtering framework: (1) IObjectTest interface with a single boolean test(Object o) method and (2) an implementation of Iterator (let's call it FilteringIterator) which is initialized with another Iterator and an IObjectTest instance: new FilteringIterator(myIterator, myTest). Your FilteringIterator will then allow iteration over 'myIterator', but skipping any objects which don't pass the 'myTest' test. Create a simple unit test for this framework.

Assumptions Made:
- FilteringIterator does not need to be thread-safe; it's expected to be used only in a single-threaded context.
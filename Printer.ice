module Demo
{
    class Response{
        long responseTime;
        string value;
        long latency;
    }
    interface Printer
    {
        Response printString(string s);
    }
}
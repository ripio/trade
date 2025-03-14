using System;
using System.Threading.Tasks;

class Program
{
    static async Task Main(string[] args)
    {
        if (args.Length == 0)
        {
            Console.WriteLine("Please specify 'Get' or 'Post' as a command line argument.");
            return;
        }

        string command = args[0].ToLower();
        
        switch (command)
        {
            case "get":
                await Get.ExecuteGet();
                break;
            case "post":
                await Post.ExecutePost();
                break;
            default:
                Console.WriteLine($"Unknown command: {command}. Please use 'Get' or 'Post'.");
                break;
        }
    }
}

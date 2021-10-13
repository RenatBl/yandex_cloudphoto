import com.beust.jcommander.JCommander;
import lombok.extern.slf4j.Slf4j;
import service.CommandsProcessingService;
import service.impl.CommandsProcessingServiceImpl;
import util.Args;

import java.util.Arrays;

public class MainClass {

    private static final CommandsProcessingService commandsProcessingService = new CommandsProcessingServiceImpl();

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        args = Arrays.copyOfRange(args, 1, args.length);
        Args args1 = new Args();
        JCommander jCommander = new JCommander(args1);
        jCommander.setProgramName("cloudphoto");
        jCommander.parse(args);
        System.out.println("Properties: " + args1);
        commandsProcessingService.process(args1);
    }
}

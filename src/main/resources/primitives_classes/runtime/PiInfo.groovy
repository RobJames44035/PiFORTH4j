package primitives_classes.runtime

import com.pi4j.common.Descriptor
import com.rajames.forth.dictionary.Word
import com.rajames.forth.runtime.AbstractRuntime
import com.rajames.forth.runtime.ForthInterpreter

import com.pi4j.context.Context
import com.pi4j.platform.Platform
import com.pi4j.platform.Platforms
import com.pi4j.provider.Providers
import com.pi4j.registry.Registry
import com.pi4j.util.Console
import com.pi4j.Pi4J

class PiInfo extends AbstractRuntime {
    @Override
    Object execute(ForthInterpreter interpreter, Word word, Word parentWord) {
        Context pi4j = interpreter.forthRepl.piContext
        if(pi4j != null) {
            Platforms platforms = pi4j.platforms()
            Providers providers = pi4j.providers()
            Registry registry = pi4j.registry()
            Descriptor descriptor = platforms.describe()
            println()
            println("============================================================")
            println(descriptor.name())
            println("============================================================")
            platforms.describe().print(System.out)
            println("============================================================")
            providers.describe().print(System.out)
            println("============================================================")
            registry.describe().print(System.out)
            println("============================================================")
        } else {
            print("Pi Context unset. Please  enter 'pi-context' and try again.")
        }
        return null
    }


}

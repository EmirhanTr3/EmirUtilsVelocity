package xyz.emirdev.echologic.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import xyz.emirdev.echologic.ELCommandException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressArgumentType implements CustomArgumentType<InetAddress, String> {

    @Override
    public InetAddress convert(String nativeType) throws CommandSyntaxException {
        try {
            return InetAddress.getByName(nativeType);
        } catch (UnknownHostException e) {
            throw new ELCommandException(
                    "<red>Invalid IP address:</red> <yellow><ip></yellow>",
                    Placeholder.unparsed("ip", nativeType)).create();
        }
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
}

package xyz.emirdev.echologic.parameters;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.jetbrains.annotations.NotNull;

import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;
import revxrsal.commands.velocity.actor.VelocityCommandActor;
import xyz.emirdev.echologic.ELCommandException;

import java.net.InetSocketAddress;

public class InetSocketAddressParameterType implements ParameterType<VelocityCommandActor, InetSocketAddress> {

    @Override
    public InetSocketAddress parse(@NotNull MutableStringStream input,
            @NotNull ExecutionContext<@NotNull VelocityCommandActor> context) {
        String ip = input.readString();

        if (!ip.matches(
                "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}(:[0-9]{1,5})?")) {
            throw new ELCommandException(
                    "<red>Invalid IP address:</red> <yellow><ip></yellow>",
                    Placeholder.unparsed("ip", ip));
        }

        int port = 25565;
        if (ip.contains(":")) {
            int sPort = Integer.valueOf(ip.split(":")[1]);
            if (sPort < 0 || sPort > 0xFFFF) {
                throw new ELCommandException(
                        "<red>Invalid port:</red> <yellow><port></yellow>",
                        Placeholder.unparsed("port", String.valueOf(sPort)));
            }
            port = sPort;
        }

        return new InetSocketAddress(ip, port);
    }

    @Override
    public boolean isGreedy() {
        return false;
    }
}

package entities;

import java.util.HashSet;
import java.util.Set;

public class Peer {
    private String address;
    private Set<String> knownPeers;
    private String dir;


    public Peer(String address, String dir) {
        this.address = address;
        this.dir = dir;
        this.knownPeers = new HashSet<>();
    }

    public Peer(String address, Set<String> knownPeers, String dir) {
        this.address = address;
        this.knownPeers = knownPeers;
        this.dir = dir;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<String> getKnownPeers() {
        return knownPeers;
    }

    public void setKnownPeers(Set<String> knownPeers) {
        this.knownPeers = knownPeers;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "address='" + address + '\'' +
                ", knownPeers=" + knownPeers +
                ", dir='" + dir + '\'' +
                '}';
    }
}

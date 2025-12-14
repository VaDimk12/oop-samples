import com.google.inject.Inject;

public class ClientFactory {
    private final ClientService clientService;

    @Inject
    public ClientFactory(ClientService clientService) {
        this.clientService = clientService;
    }

    public Client create(int id, String name, String contactInfo) {
        return new Client(id, name, contactInfo, clientService);
    }
}
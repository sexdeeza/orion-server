package client.inventory;

@lombok.extern.slf4j.Slf4j
public class InventoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Creates a new instance of InventoryException */
    public InventoryException() {
        super();
    }

    public InventoryException(String msg) {
        super(msg);
    }
}

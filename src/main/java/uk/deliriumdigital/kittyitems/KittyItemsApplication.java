package uk.deliriumdigital.kittyitems;

import com.nftco.flow.sdk.cadence.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KittyItemsApplication {

    public static void main(String[] args) {
        SpringApplication.run(KittyItemsApplication.class, args);

        var e = new Field[1];
        ArrayField arrayField = new ArrayField(e);
        Field<Field<?>[]> arrayField2 = new ArrayField(e);

        OptionalField optionalField = new OptionalField(new BooleanField(false));

        PathValue pathValue = new PathValue("abc", "def");
        PathField pathField = new PathField(pathValue);

    }

}

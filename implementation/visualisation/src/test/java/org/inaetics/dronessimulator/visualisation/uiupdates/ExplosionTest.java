package org.inaetics.dronessimulator.visualisation.uiupdates;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExplosionTest {
    @Test
    public void execute() throws Exception {
        Pane pane = mock(Pane.class);
        LinkedList<Node> paneChildren = new LinkedList<>();
        when(pane.getChildren()).thenReturn(new ObservableListWrapper<>(paneChildren));
        ImageView imageView = mock(ImageView.class);
        Explosion explosion = new Explosion(0.5, imageView);
        explosion.execute(pane);

        //The explosion is correctly added to the list
        Assert.assertThat(paneChildren, hasSize(greaterThan(0)));

    }

}
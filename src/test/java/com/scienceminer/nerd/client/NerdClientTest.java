package com.scienceminer.nerd.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class NerdClientTest {

    NerdClient target;

    @Before
    public void setUp() throws Exception {

        target = new NerdClient();

    }

    @Test
    @Ignore
    public void test_shortText() throws Exception {

        target = new NerdClient("http://nerd.huma-num.fr/nerd/service");
        System.out.println(target.disambiguateText("We were walking to Washington and then we arrive to Milan, but nobody was there. ", null));

    }

    @Test
    @Ignore
    public void test_LongerText() throws Exception {
        System.out.println(target.disambiguateText("Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \n\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\n\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day. A general Allied retreat followed, resulting in more clashes such as the Battle of Le Cateau, the Siege of Maubeuge and the Battle of St. Quentin (Guise). \n\nThe German army came within 70 km (43 mi) of Paris, but at the First Battle of the Marne (6–12 September), French and British troops were able to force a German retreat by exploiting a gap which appeared between the 1st and 2nd Armies, ending the German advance into France. The German army retreated north of the Aisne River and dug in there, establishing the beginnings of a static western front that was to last for the next three years. Following this German setback, the opposing forces tried to outflank each other in the Race for the Sea, and quickly extended their trench systems from the North Sea to the Swiss frontier. The resulting German-occupied territory held 64% of France's pig-iron production, 24% of its steel manufacturing, dealing a serious, but not crippling setback to French industry.", null));
    }

    @Test
    @Ignore
    public void test_processQuery() throws Exception {
        String text = "Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \n\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\n\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day. A general Allied retreat followed, resulting in more clashes such as the Battle of Le Cateau, the Siege of Maubeuge and the Battle of St. Quentin (Guise). \n\nThe German army came within 70 km (43 mi) of Paris, but at the First Battle of the Marne (6–12 September), French and British troops were able to force a German retreat by exploiting a gap which appeared between the 1st and 2nd Armies, ending the German advance into France. The German army retreated north of the Aisne River and dug in there, establishing the beginnings of a static western front that was to last for the next three years. Following this German setback, the opposing forces tried to outflank each other in the Race for the Sea, and quickly extended their trench systems from the North Sea to the Swiss frontier. The resulting German-occupied territory held 64% of France's pig-iron production, 24% of its steel manufacturing, dealing a serious, but not crippling setback to French industry.";

        final ObjectNode segmentedText = target.segment(text);


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode query = mapper.createObjectNode();
        query.put("text", text);
        query.set("sentences", segmentedText.get("sentences"));
        final ArrayNode processSentence = mapper.createArrayNode().add(1).add(2);
        query.set("processSentence", processSentence);

        System.out.println(target.processQuery(query, true));
    }

    @Test
    @Ignore
    public void test_segment() throws Exception {
        final ObjectNode segment = target.segment("Austria invaded and fought the Serbian army at the Battle of Cer and Battle of Kolubara beginning on 12 August. \n\nThe army, led by general Paul von Hindenburg defeated Russia in a series of battles collectively known as the First Battle of Tannenberg (17 August – 2 September). But the failed Russian invasion, causing the fresh German troops to move to the east, allowed the tactical Allied victory at the First Battle of the Marne. \\n\\nUnfortunately for the Allies, the pro-German King Constantine I dismissed the pro-Allied government of E. Venizelos before the Allied expeditionary force could arrive. Beginning in 1915, the Italians under Cadorna mounted eleven offensives on the Isonzo front along the Isonzo River, northeast of Trieste.\\n\\n At the Siege of Maubeuge about 40000 French soldiers surrendered, at the battle of Galicia Russians took about 100-120000 Austrian captives, at the Brusilov Offensive about 325 000 to 417 000 Germans and Austrians surrendered to Russians, at the Battle of Tannenberg 92,000 Russians surrendered.\n\n After marching through Belgium, Luxembourg and the Ardennes, the German Army advanced, in the latter half of August, into northern France where they met both the French army, under Joseph Joffre, and the initial six divisions of the British Expeditionary Force, under Sir John French. A series of engagements known as the Battle of the Frontiers ensued. Key battles included the Battle of Charleroi and the Battle of Mons. In the former battle the French 5th Army was almost destroyed by the German 2nd and 3rd Armies and the latter delayed the German advance by a day. A general Allied retreat followed, resulting in more clashes such as the Battle of Le Cateau, the Siege of Maubeuge and the Battle of St. Quentin (Guise). \n\nThe German army came within 70 km (43 mi) of Paris, but at the First Battle of the Marne (6–12 September), French and British troops were able to force a German retreat by exploiting a gap which appeared between the 1st and 2nd Armies, ending the German advance into France. The German army retreated north of the Aisne River and dug in there, establishing the beginnings of a static western front that was to last for the next three years. Following this German setback, the opposing forces tried to outflank each other in the Race for the Sea, and quickly extended their trench systems from the North Sea to the Swiss frontier. The resulting German-occupied territory held 64% of France's pig-iron production, 24% of its steel manufacturing, dealing a serious, but not crippling setback to French industry.\n");

        ArrayNode sentencesArray = (ArrayNode) segment.get("sentences");
        assertThat(sentencesArray, is(notNullValue()));
        assertThat(sentencesArray.size(), is(17) );
    }

}

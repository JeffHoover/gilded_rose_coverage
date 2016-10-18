package com.ford.acoe;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class GildedRoseTest {

	// TODO - Might make sense to have these static on the GildedRose class:
	// GildedRose.AGED_BRIE ...
	private static final String AGED_BRIE = "Aged Brie";
	private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
	private static final String BACKSTAGE_PASSES = "Backstage passes to a TAFKAL80ETC concert";
	private static final String ORDINARY_ITEM = "ordinary";

	// Sulfuras" is a legendary item and as such its Quality is 80 and it never
	// alters.
	private static final int SULFURAS_QUALITY = 80;

	private int SELLIN = 11;
	private int QUALITY = 10;

	private GildedRose app;

	@Before
	public void setup() {
		app = null;
	}

	// - At the end of each day our system lowers Quality for every item
	@Test
	public void ordinaryItemQualityDecreasesBy1Daily() {
		QUALITY = 12;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY - 1, app.items[0]);
	}

	// - At the end of each day our system lowers Quality for every item
	@Test
	public void ordinaryItemQualityDecreasesBy1Daily_EvenIfQualityStartsAbove50() {
		QUALITY = 55;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY - 1, app.items[0]);
	}

	// - At the end of each day our system lowers Sellin for every item
	@Test
	public void ordinaryItemSellInDecreasesBy1Daily() {
		SELLIN = 10;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));
		app.updateQuality();

		assertSellin(SELLIN - 1, app.items[0]);
	}

	// - At the end of each day our system lowers Sellin for every item
	@Test
	public void ordinaryItemSellinContinuesToDecrease_EvenAfterSellinIsZero() {
		SELLIN = 2;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();

		app.updateQuality();
		app.updateQuality();
		app.updateQuality();

		assertSellin(SELLIN - 5, app.items[0]);
	}

	// - Once the sell by date has passed, Quality degrades twice as fast
	@Test
	public void ordinaryItemQualityDecreasesTwiceAsFastAfterSellinIsZero() {
		SELLIN = 2;
		QUALITY = 20;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();

		app.updateQuality();
		app.updateQuality();
		app.updateQuality();

		int expectedQuality = QUALITY - 1 - 1 - 2 - 2 - 2;

		assertQuality(expectedQuality, app.items[0]);
	}

	// - The Quality of an item is never negative
	@Test
	public void ordinaryItemQualityNeverGoesNegative() {
		QUALITY = 1;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();
		app.updateQuality();

		assertQuality(0, app.items[0]);
	}

	// - "Aged Brie" actually increases in Quality the older it gets
	@Test
	public void agedBrieQualityIncreasesDaily() {

		QUALITY = 10;
		app = initGildedRose(createItem(AGED_BRIE, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();

		assertQuality(QUALITY + 2, app.items[0]);
	}

	// - The Quality of an item is never more than 50
	@Test
	public void agedBrieQualityNeverIncreasesPast50() {

		QUALITY = 49;
		app = initGildedRose(createItem(AGED_BRIE, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();

		assertQuality(50, app.items[0]);
	}

	// - "Sulfuras", being a legendary item, never has to be sold
	@Test
	public void sulfurasNeverDecreasesSellIn() {
		SELLIN = 2;
		app = initGildedRose(createItem(SULFURAS, SELLIN, SULFURAS_QUALITY));
		app.updateQuality();
		app.updateQuality();

		assertSellin(SELLIN, app.items[0]);
	}

	// - "Sulfuras", being a legendary item, never decreases in Quality
	@Test
	public void sulfurasNeverDecreasesQuality() {
		QUALITY = 49;
		app = initGildedRose(createItem(SULFURAS, SELLIN, SULFURAS_QUALITY));

		app.updateQuality();
		app.updateQuality();

		assertQuality(SULFURAS_QUALITY, app.items[0]);
	}

	// - "Backstage passes" increases in Quality as its SellIn value approaches;
	@Test
	public void backStagePassesIncreasesQualityBy1WhenElevenDaysBeforeTheConcert() {
		QUALITY = 2;
		SELLIN = 11;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 1, app.items[0]);
	}

	// - "Backstage passes" Quality increases by 2 when there are 10 days or
	// less
	@Test
	public void backStagePassesIncreasesQualityBy2WhenTenDaysBeforeTheConcert() {
		QUALITY = 2;
		SELLIN = 10;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 2, app.items[0]);
	}

	// - "Backstage passes" Quality increases by 2 when there are 10 days or
	// less
	@Test
	public void backStagePassesIncreasesQualityBy2WhenSixDaysBeforeTheConcert() {
		QUALITY = 2;
		SELLIN = 6;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 2, app.items[0]);
	}

	// - "Backstage passes" Quality increases by 3 when there are 5 days or less
	@Test
	public void backStagePassesIncreasesQualityBy3WhenFiveDaysBeforeTheConcert() {
		int SELLIN = 5;
		int QUALITY = 20;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 3, app.items[0]);
	}

	// - "Backstage passes" Quality increases by 3 when there are 5 days or less
	@Test
	public void backStagePassesIncreasesQualityBy3WhenOneDayBeforeTheConcert() {
		int SELLIN = 1;
		int QUALITY = 20;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 3, app.items[0]);
	}

	// - "Backstage passes" Quality drops to 0 after the concert
	@Test
	public void backStagePassesQualityGoesTo0AtTheEndOfDayOfTheConcert() {
		int SELLIN = 0;
		int QUALITY = 20;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(0, app.items[0]);
	}

	// - "Backstage passes" Quality drops to 0 after the concert
	@Test
	public void backStagePassesQualityIsStillZeroWhenFiveDaysAfterTheConcert() {
		int SELLIN = -5;
		int QUALITY = 20;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(0, app.items[0]);
	}

	@Test
	public void itemCanPrettyPrint() {
		app = initGildedRose(createItem(SULFURAS, SELLIN, SULFURAS_QUALITY));

		String expectedString = "Sulfuras, Hand of Ragnaros, " + SELLIN + ", " + SULFURAS_QUALITY;
		assertEquals(expectedString, app.items[0].toString());
	}
	
	@Test
	public void UNDOCUMENTED_agedBrieStartingAbove50QualityDoesNotChangeQuality() {
		QUALITY = 55;
		app = initGildedRose(createItem(AGED_BRIE, SELLIN, QUALITY));

		app.updateQuality();
		app.updateQuality();

		assertQuality(QUALITY, app.items[0]);
	}

	private Item createItem(String name, int sellin, int quality) {
		return new Item(name, sellin, quality);
	}

	private GildedRose initGildedRose(Item item) {
		return new GildedRose(new Item[] { item });
	}

	private void assertQuality(int expectedQuality, Item item) {
		assertEquals(expectedQuality, item.quality);
	}

	private void assertSellin(int expectedSellin, Item item) {
		assertEquals(expectedSellin, item.sellIn);
	}

	@Test
	public void causeNegativePathCoverageOnLines31and37() {
		QUALITY = 49;
		SELLIN = 4;
		app = initGildedRose(createItem(BACKSTAGE_PASSES, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 1, app.items[0]);
	}

	@Test
	public void causeCoverageOnLine62AndPositivePathOnLine61() {
		QUALITY = 49;
		SELLIN = -1;
		app = initGildedRose(createItem(AGED_BRIE, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(QUALITY + 1, app.items[0]);
	}

	@Test
	public void w() {
		QUALITY = -1;
		SELLIN = -1;
		app = initGildedRose(createItem(AGED_BRIE, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(1, app.items[0]);
	}

	@Test
	public void v() {
		QUALITY = -1;
		SELLIN = -1;
		app = initGildedRose(createItem(ORDINARY_ITEM, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(-1, app.items[0]);
	}

	@Test
	public void u() {
		QUALITY = 5;
		SELLIN = -1;
		app = initGildedRose(createItem(SULFURAS, SELLIN, QUALITY));

		app.updateQuality();

		assertQuality(5, app.items[0]);
	}

}

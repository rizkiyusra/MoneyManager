package com.example.moneymanager.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.moneymanager.data.local.asset.AssetDao
import com.example.moneymanager.data.local.asset.AssetEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AssetDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: MoneyManagerDatabase
    private lateinit var assetDao: AssetDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MoneyManagerDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        assetDao = database.assetDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAssetAndGetById() = runTest {
        val asset = AssetEntity(
            assetName = "Test Bank",
            assetType = "BANK",
            currentBalance = 1000.0,
            balanceUnit = "IDR",
            currencySymbol = "Rp",
            accountNumber = "12345",
            bankName = "Bank Of Testing",
            lastPriceUpdate = 2500.0,
            priceSource = "BANK",
        )
        val generatedId = assetDao.insertAsset(asset)

        val retrievedAsset = assetDao.getAssetById(generatedId.toInt())
        assertThat(retrievedAsset).isNotNull()
        assertThat(retrievedAsset?.assetId).isEqualTo(generatedId.toInt())

        assertThat(retrievedAsset?.assetName).isEqualTo(asset.assetName)
        assertThat(retrievedAsset?.assetType).isEqualTo(asset.assetType)
        assertThat(retrievedAsset?.currentBalance).isEqualTo(asset.currentBalance)
        assertThat(retrievedAsset?.balanceUnit).isEqualTo(asset.balanceUnit)
        assertThat(retrievedAsset?.currencySymbol).isEqualTo(asset.currencySymbol)
        assertThat(retrievedAsset?.accountNumber).isEqualTo(asset.accountNumber)
        assertThat(retrievedAsset?.bankName).isEqualTo(asset.bankName)

        assertThat(retrievedAsset?.isActive).isTrue()
    }

    @Test
    fun getAllAssetsWhenNoAssetsInserted() = runTest {
        val allAssets = assetDao.getAssets().first()
        assertThat(allAssets).isEmpty()
    }

    @Test
    fun getAllAssetsReturnsAllInsertedAssets() = runTest {
        val asset1ToInsert = AssetEntity(
            assetName = "Bank A",
            currentBalance = 1000.0,
            assetType = "BANK",
            balanceUnit = "IDR",
            currencySymbol = "Rp"
        )
        val asset2ToInsert = AssetEntity(
            assetName = "Cash Dompet",
            currentBalance = 500.50,
            assetType = "CASH",
            balanceUnit = "IDR",
            currencySymbol = "Rp"
        )

        val id1 = assetDao.insertAsset(asset1ToInsert)
        val id2 = assetDao.insertAsset(asset2ToInsert)

        val allAssets = assetDao.getAssets().first()
        assertThat(allAssets).hasSize(2)

        val expectedAsset1 = asset1ToInsert.copy(assetId = id1.toInt())
        val expectedAsset2 = asset2ToInsert.copy(assetId = id2.toInt())

        if (id1.toInt() < id2.toInt()) {
            assertThat(allAssets).containsExactly(expectedAsset2, expectedAsset1).inOrder()
        } else {
            assertThat(allAssets).containsExactly(expectedAsset1, expectedAsset2).inOrder()
        }
    }

    @Test
    fun getAssetByIdWhenAssetDoesNotExist() = runTest {
        val retrievedAsset = assetDao.observeAssetById(999).first()
        assertThat(retrievedAsset).isNull()
    }

    @Test
    fun updateAssetAndGetById() = runTest {
        val initialAsset = AssetEntity(
            assetName = "Initial Name",
            assetType = "SAVINGS",
            currentBalance = 100.0,
            balanceUnit = "EUR",
            currencySymbol = "€"
        )
        val generatedId = assetDao.insertAsset(initialAsset)

        val updatedAsset = AssetEntity(
            assetId = generatedId.toInt(),
            assetName = "Updated Name",
            assetType = "CHECKING",
            currentBalance = 250.50,
            balanceUnit = "EUR",
            currencySymbol = "€",
            accountNumber = initialAsset.accountNumber,
            bankName = initialAsset.bankName,
            isActive = initialAsset.isActive,
            sortOrder = initialAsset.sortOrder,
            createdDate = initialAsset.createdDate,
            lastModified = System.currentTimeMillis()
        )

        assetDao.updateAsset(updatedAsset)

        val retrievedAsset = assetDao.observeAssetById(generatedId.toInt()).first()
        assertThat(retrievedAsset).isNotNull()
        assertThat(retrievedAsset?.assetName).isEqualTo("Updated Name")
        assertThat(retrievedAsset?.currentBalance).isEqualTo(250.50)
        assertThat(retrievedAsset?.assetType).isEqualTo("CHECKING")
        assertThat(retrievedAsset?.lastModified).isNotEqualTo(retrievedAsset?.createdDate)
    }

    @Test
    fun deleteAssetAndEnsureItsNotRetrievable() = runTest {
        val assetToDelete = AssetEntity(
            assetName = "To Be Deleted",
            assetType = "WALLET",
            currentBalance = 50.0,
            balanceUnit = "GBP",
            currencySymbol = "£"
        )
        val generatedId = assetDao.insertAsset(assetToDelete)

        val assetForDeletion = assetToDelete.copy(assetId = generatedId.toInt())
        assetDao.deleteAsset(assetForDeletion)

        val retrievedAsset = assetDao.observeAssetById(generatedId.toInt()).first()
        assertThat(retrievedAsset).isNull()

        val allAssets = assetDao.getAssets().first()
        assertThat(allAssets.find { it.assetId == generatedId.toInt() }).isNull()
    }

    @Test
    fun checkAssetNameExistsWhenItDoes() = runTest {
        val asset = AssetEntity(assetName = "Unique Asset Name", assetType = "Test", currentBalance = 0.0, balanceUnit = "X", currencySymbol = "Y")
        val id = assetDao.insertAsset(asset)

        val count = assetDao.checkAssetNameExists("Unique Asset Name", excludeId = (id + 1).toInt())
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun checkAssetNameExistsWhenUpdatingWithSameName() = runTest {
        val asset = AssetEntity(assetName = "Existing Name", assetType = "Test", currentBalance = 0.0, balanceUnit = "X", currencySymbol = "Y")
        val id = assetDao.insertAsset(asset)

        val count = assetDao.checkAssetNameExists("Existing Name", excludeId = id.toInt())
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun checkAssetNameExistsWhenItDoesNotExist() = runTest {
        val count = assetDao.checkAssetNameExists("Non Existent Name")
        assertThat(count).isEqualTo(0)
    }
}

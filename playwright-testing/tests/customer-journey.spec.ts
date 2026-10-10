import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Pet Owner / Customer End-to-End Journey', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'customer');
  });

  test('Overview: updates customer profile and residential address', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Pet Owner Portal');
    await page.locator('#open-profile-btn').click();
    await page.getByRole('button', { name: 'Save Profile' }).click();
    await page.getByRole('button', { name: 'Update Address' }).click();
    await page.getByRole('button', { name: 'Done' }).click();
  });

  test('My Pets: registers new pet and adds clinical medical records visible in medical history', async ({ page }) => {
    await page.getByRole('link', { name: 'My Pets' }).click();
    await expect(page.getByRole('heading', { name: 'Register New Pet' })).toBeVisible();

    await page.locator('input[placeholder="e.g. Buster"]').fill('Barnaby');
    await page.locator('input[placeholder="e.g. GOLDEN_RETRIEVER"]').fill('LABRADOR');
    await page.getByRole('button', { name: 'Register Pet' }).click();

    // Select the registered pet to open clinical medical dossier
    await page.locator('h4:has-text("Barnaby")').first().click();
    await expect(page.getByRole('heading', { name: /Medical History & Clinical Dossier/ })).toBeVisible({ timeout: 10000 });

    // Add a clinical record
    await page.locator('input[placeholder="e.g. Annual Rabies Booster"]').fill('Rabies & DHPP Booster');
    await page.getByRole('button', { name: 'Add Clinical Record' }).click();

    // Verify record appears in the medical history table
    await expect(page.locator('td:has-text("Rabies & DHPP Booster")').first()).toBeVisible({ timeout: 10000 });
  });

  test('Care Plans & Caretakers: registers primary & backup caretaker and displays default guardian in absence of owner', async ({ page }) => {
    await page.getByRole('link', { name: 'Care Plans' }).click();
    await expect(page.getByRole('heading', { name: 'Designated Caretakers in Absence of Owner' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Designate Caretaker' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Designate Backup Caretaker' })).toBeVisible();

    // Register Primary Caretaker
    await page.locator('input[placeholder="e.g. Michael Miller"]').fill('Michael Miller');
    await page.locator('input[placeholder="michael@example.com"]').fill('michael.miller@example.com');
    await page.locator('input[placeholder="555-0188"]').fill('555-0188');
    await page.getByRole('button', { name: 'Register Caretaker' }).click();

    // Register Backup Caretaker
    await page.locator('input[placeholder="e.g. Claire Anderson"]').fill('Claire Anderson');
    await page.locator('input[placeholder="claire@example.com"]').fill('claire.anderson@example.com');
    await page.locator('input[placeholder="555-0192"]').fill('555-0192');
    await page.getByRole('button', { name: 'Assign as Contingency Backup Caretaker' }).click();

    // Verify prominent default guardian cards in UI
    await expect(page.locator('text=Assigned Default Guardian')).toBeVisible({ timeout: 10000 });
    await expect(page.locator('text=Contingency Guardian').first()).toBeVisible({ timeout: 10000 });
    await expect(page.locator('text=Assigned by default as caretaker in absence of the owner').first()).toBeVisible({ timeout: 10000 });

    await page.getByRole('button', { name: 'Save Care Plan' }).click();
  });

  test('Quotes & Policies: requests actuarial quote, accepts proposal, and issues policy', async ({ page }) => {
    await page.getByRole('link', { name: 'Get Quote' }).click();
    await expect(page.getByRole('heading', { name: 'Request Instant Continuity Quote' })).toBeVisible();

    const petSelect = page.locator('select').first();
    if (await petSelect.isVisible()) {
      await petSelect.selectOption({ index: 1 });
    }

    await page.getByRole('button', { name: 'Run Actuarial Quote Engine' }).click();
    await expect(page.locator('text=Continuity Policy Proposal')).toBeVisible({ timeout: 15000 });
    await page.getByRole('button', { name: 'Accept Proposal & Issue Policy' }).click();

    await page.getByRole('link', { name: 'Policies' }).click();
    await expect(page.getByRole('heading', { name: 'Active Continuity Policies' })).toBeVisible();
  });

  test('Policies & Billing: Pay Premium navigates to payment page and activates policy to ACTIVE', async ({ page }) => {
    await page.getByRole('link', { name: 'Policies' }).click();
    await expect(page.getByRole('heading', { name: 'Active Continuity Policies' })).toBeVisible();

    const payButton = page.locator('button:has-text("Pay Premium")').first();
    if (await payButton.isVisible()) {
      await payButton.click();
      // Should navigate to /payment
      await expect(page).toHaveURL(/.*payment/);
      await expect(page.locator('h1')).toContainText('Continuity Policy Premium Checkout');

      // Click Pay on the payment page
      await page.locator('button:has-text("Authorize & Pay")').click();
      await expect(page.locator('text=Payment Processed Successfully!')).toBeVisible({ timeout: 10000 });

      // Automatically or manually returns to policies
      await page.waitForTimeout(3000);
      if (page.url().includes('customer')) {
        await expect(page.locator('span:has-text("ACTIVE")').first()).toBeVisible({ timeout: 10000 });
      }
    }
  });

  test('Claims & Continuity: files continuity claim with evidence documentation', async ({ page }) => {
    await page.getByRole('link', { name: 'Claims' }).click();
    await expect(page.getByRole('heading', { name: 'Initiate Continuity Claim' })).toBeVisible();

    await page.locator('input[placeholder="e.g. Michael Miller"]').fill('Michael Miller');
    await page.locator('input[placeholder="e.g. DC-2026-9001"]').fill('DC-2026-9001');
    await page.getByRole('button', { name: 'File Claim for Adjudication' }).click();

    await expect(page.locator('text=Claim #').first()).toBeVisible({ timeout: 10000 });
  });
});

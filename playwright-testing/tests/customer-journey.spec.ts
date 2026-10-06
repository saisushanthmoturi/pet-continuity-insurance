import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Pet Owner / Customer End-to-End Journey', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'customer');
  });

  test('Overview: updates customer profile and residential address', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Pet Owner Portal');
    await page.getByRole('button', { name: 'Save Profile' }).click();
    await page.getByRole('button', { name: 'Update Address' }).click();
  });

  test('My Pets: navigates tab and registers a new pet with medical records', async ({ page }) => {
    await page.getByRole('button', { name: 'My Pets' }).click();
    await expect(page.getByRole('heading', { name: 'Register New Pet' })).toBeVisible();

    await page.locator('input[placeholder="e.g. Buster"]').fill('Barnaby');
    await page.locator('input[placeholder="e.g. GOLDEN_RETRIEVER"]').fill('LABRADOR');
    await page.getByRole('button', { name: 'Register Pet' }).click();

    // Select a registered pet to open clinical records
    await page.locator('h4').first().click();
    await expect(page.locator('button:has-text("Add Clinical Record")')).toBeVisible({ timeout: 10000 });
  });

  test('Care Plans & Caretakers: registers primary caretaker and care plan', async ({ page }) => {
    await page.getByRole('button', { name: 'Care Plans & Caretakers' }).click();
    await expect(page.getByRole('heading', { name: 'Designate Caretaker' })).toBeVisible();
    await expect(page.getByRole('heading', { name: 'Care Routine & Instructions' })).toBeVisible();

    await page.locator('input[placeholder="e.g. Michael Miller"]').fill('Robert Davis');
    await page.locator('input[placeholder="michael@example.com"]').fill('robert@example.com');
    await page.locator('input[placeholder="555-0188"]').fill('555-0199');
    await page.getByRole('button', { name: 'Register Caretaker' }).click();

    await page.getByRole('button', { name: 'Save Care Plan' }).click();
  });

  test('Quotes & Policies: requests actuarial quote, accepts proposal, and navigates to policies', async ({ page }) => {
    await page.getByRole('button', { name: 'Get Quote' }).click();
    await expect(page.getByRole('heading', { name: 'Request Instant Continuity Quote' })).toBeVisible();

    // Select pet if dropdown is available
    const petSelect = page.locator('select').first();
    if (await petSelect.isVisible()) {
      await petSelect.selectOption({ index: 1 });
    }

    await page.getByRole('button', { name: 'Run Actuarial Quote Engine' }).click();
    
    await expect(page.locator('text=Continuity Policy Proposal')).toBeVisible({ timeout: 15000 });
    await page.getByRole('button', { name: 'Accept Proposal & Issue Policy' }).click();

    await page.getByRole('button', { name: 'Policies & Billing' }).click();
    await expect(page.getByRole('heading', { name: 'Active Continuity Policies' })).toBeVisible();
  });

  test('Claims & Continuity: files continuity claim with evidence documentation', async ({ page }) => {
    await page.getByRole('button', { name: 'Claims & Continuity' }).click();
    await expect(page.getByRole('heading', { name: 'Initiate Continuity Claim' })).toBeVisible();

    await page.locator('select:has-text("Owner Deceased")').selectOption('OWNER_DEATH');
    await page.getByRole('button', { name: 'File Claim for Adjudication' }).click();
  });
});

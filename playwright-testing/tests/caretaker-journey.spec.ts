import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Designated Caretaker Fiduciary Journey', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'caretaker');
  });

  test('Pet Custody: submits physical custody confirmation and observations', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Caretaker Fiduciary Portal');
    await expect(page.getByRole('heading', { name: 'Custodial Receipt & Verification' })).toBeVisible();

    await page.getByRole('button', { name: 'Submit Physical Custody Confirmation' }).click();
    await expect(page.locator('text=Custody Verified')).toBeVisible();
  });

  test('Care Trust & Disbursements: inspects balance and claims monthly allowance', async ({ page }) => {
    await page.getByRole('button', { name: 'Care Trust & Disbursements' }).click();
    await expect(page.locator('text=Allocated Trust Fund')).toBeVisible();
    await expect(page.locator('text=Current Reserve Balance')).toBeVisible();

    await page.getByRole('button', { name: 'Disburse Current Monthly Allowance ($600.00)' }).click();
  });

  test('Expense Reimbursements: files supplemental veterinary expense', async ({ page }) => {
    await page.getByRole('button', { name: 'Expense Reimbursements' }).click();
    await expect(page.getByRole('heading', { name: 'File Extra Expense' })).toBeVisible();

    await page.getByRole('button', { name: 'Submit for Reimbursement' }).click();
  });

  test('Backup Transfer: submits secondary caretaker custody transfer request', async ({ page }) => {
    await page.getByRole('button', { name: 'Backup Transfer' }).click();
    await expect(page.getByRole('heading', { name: 'Initiate Backup Caretaker Transfer' })).toBeVisible();

    await page.getByRole('button', { name: 'Execute Backup Caretaker Handover' }).click();
    await expect(page.locator('text=Custody transfer request submitted successfully')).toBeVisible();
  });
});

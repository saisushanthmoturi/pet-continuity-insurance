import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Underwriting Risk Officer End-to-End Journey', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'underwriter');
  });

  test('Underwriter dashboard renders evaluation queue', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Underwriting & Risk Console');
    await expect(page.getByRole('heading', { name: 'Underwriting Evaluation Queue' })).toBeVisible();
  });

  test('Rating Rules: configures new actuarial multiplier rule', async ({ page }) => {
    await page.getByRole('button', { name: 'Rating Rules & Multipliers' }).click();
    await expect(page.getByRole('heading', { name: 'Configure Rating Rule' })).toBeVisible();

    await page.getByRole('button', { name: 'Save Rating Rule' }).click();
  });

  test('Pet Risk Re-Assessment: executes clinical risk recalculation engine', async ({ page }) => {
    await page.getByRole('button', { name: 'Pet Risk Re-Assessment' }).click();
    await expect(page.getByRole('heading', { name: 'Pet Clinical Re-Assessment Engine' })).toBeVisible();

    await page.getByRole('button', { name: 'Execute Clinical Risk Recalculation' }).click();
    await expect(page.locator('text=Recalculation Results')).toBeVisible({ timeout: 15000 });
    await expect(page.locator('text=Risk Multiplier:')).toBeVisible();
  });
});

import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Claims Adjuster End-to-End Journey', () => {
  test.beforeEach(async ({ page }) => {
    await loginAs(page, 'claims');
  });

  test('Claims console loads queue and handles adjudication verification', async ({ page }) => {
    await expect(page.locator('h1')).toContainText('Claims Adjudication Desk');
    await expect(page.getByRole('heading', { name: 'Pending Claims Queue' })).toBeVisible();

    await page.getByRole('button', { name: 'Adjudication & Verification' }).click();
    await expect(page.locator('text=Select a claim from the queue')).toBeVisible();
  });
});

import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('RBAC Route Guard & Security Suite', () => {
  test('Prevents unauthenticated users from accessing protected portal routes', async ({ page }) => {
    const protectedRoutes = ['/customer', '/underwriter', '/claims', '/caretaker', '/admin'];
    
    for (const route of protectedRoutes) {
      await page.goto(route);
      await expect(page).toHaveURL(/.*\/auth\/login/);
    }
  });

  test('Prevents customer from accessing privileged underwriter portal', async ({ page }) => {
    await loginAs(page, 'customer');
    await page.goto('/underwriter');
    await expect(page).toHaveURL(/.*\/auth\/login/);
  });

  test('Prevents customer from accessing privileged admin portal', async ({ page }) => {
    await loginAs(page, 'customer');
    await page.goto('/admin');
    await expect(page).toHaveURL(/.*\/auth\/login/);
  });
});

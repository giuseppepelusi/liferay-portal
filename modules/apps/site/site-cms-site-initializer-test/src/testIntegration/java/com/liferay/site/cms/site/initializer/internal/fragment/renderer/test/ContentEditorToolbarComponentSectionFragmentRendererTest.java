/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.constants.LayoutDisplayPageWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Giuseppe Pelusi
 * @modules/apps/oauth2-provider/oauth2-provider-service/src/main/java/com/liferay/oauth2/provider/service/persistence/impl/OAuth2AuthorizationPersistenceImpl.java Giuseppe Pelusi
 */
@RunWith(Arquillian.class)
public class ContentEditorToolbarComponentSectionFragmentRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(new LiferayIntegrationTestRule());

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_originalLanguage = (Language)ReflectionTestUtil.getFieldValue(
			_fragmentRenderer, "language");

		ReflectionTestUtil.setFieldValue(
			_fragmentRenderer, "language", _mockLanguage);

		ReflectionTestUtil.setFieldValue(
			_fragmentRenderer, "_layoutPageTemplateEntryLocalService",
			_mockLayoutPageTemplateEntryLocalService);
	}

	@Test
	public void testGetPropsHeaderTitleWhenIsNewTrue() throws Exception {
		_testGetPropsHeaderTitle(false, true, "new-x");
	}

	@Test
	public void testGetPropsHeaderTitleWhenIsNewFalse() throws Exception {
		_testGetPropsHeaderTitle(false, false, "edit-x");
	}

	@Test
	public void testGetPropsHeaderTitleWhenTranslation() throws Exception {
		_testGetPropsHeaderTitle(true, false, "translate-x");
	}

	private void _testGetPropsHeaderTitle(
			boolean translation, boolean isNew, String expectedKey)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("isNew", String.valueOf(isNew));

		LayoutDisplayPageObjectProvider mockLayoutDisplayPageObjectProvider =
			Mockito.mock(LayoutDisplayPageObjectProvider.class);

		ObjectEntry mockObjectEntry = Mockito.mock(ObjectEntry.class);

		Mockito.when(
			mockLayoutDisplayPageObjectProvider.getDisplayObject()
		).thenReturn(
			mockObjectEntry
		);

		Mockito.when(
			mockLayoutDisplayPageObjectProvider.getTitle(Mockito.any(Locale.class))
		).thenReturn(
			"My Title"
		);

		mockHttpServletRequest.setAttribute(
			LayoutDisplayPageWebKeys.LAYOUT_DISPLAY_PAGE_OBJECT_PROVIDER,
			mockLayoutDisplayPageObjectProvider);

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompanyId(TestPropsValues.getCompanyId());
		themeDisplay.setLocale(Locale.US);

		Layout mockLayout = Mockito.mock(Layout.class);

		Mockito.when(mockLayout.getPlid()).thenReturn(12345L);

		themeDisplay.setLayout(mockLayout);

		mockHttpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		LayoutPageTemplateEntry mockLayoutPageTemplateEntry =
			Mockito.mock(LayoutPageTemplateEntry.class);

		String layoutPageTemplateEntryKey = "NOT_TRANSLATION_KEY";

		if (translation) {
			layoutPageTemplateEntryKey = "LFR_CMS_TRANSLATION_123";
		}

		Mockito.when(
			mockLayoutPageTemplateEntry.getLayoutPageTemplateEntryKey()
		).thenReturn(
			layoutPageTemplateEntryKey
		);

		Mockito.when(
			_mockLayoutPageTemplateEntryLocalService.fetchLayoutPageTemplateEntryByPlid(
				12345L)
		).thenReturn(
			mockLayoutPageTemplateEntry
		);

		Mockito.when(
			_mockLanguage.format(
				Mockito.any(Locale.class), Mockito.eq(expectedKey),
				Mockito.any(Object.class))
		).thenReturn(
			expectedKey + " My Title"
		);

		Map<String, Object> props = ReflectionTestUtil.invoke(
			_fragmentRenderer, "getProps",
			new Class<?>[] {
				FragmentRendererContext.class, HttpServletRequest.class
			},
			null, mockHttpServletRequest);

		Callable<String> headerTitleCallable = (Callable<String>)props.get(
			"headerTitle");

		String headerTitle = headerTitleCallable.call();

		Assert.assertEquals(expectedKey + " My Title", headerTitle);

		Mockito.verify(_mockLanguage).format(
			Mockito.any(Locale.class), Mockito.eq(expectedKey),
			Mockito.eq("My Title"));
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.ContentEditorToolbarComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Mock
	private Language _mockLanguage;

	@Mock
	private LayoutPageTemplateEntryLocalService _mockLayoutPageTemplateEntryLocalService;

	private Language _originalLanguage;

}

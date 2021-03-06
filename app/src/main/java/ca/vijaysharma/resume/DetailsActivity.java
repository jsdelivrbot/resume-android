package ca.vijaysharma.resume;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.vijaysharma.resume.parcelable.DetailParcel;
import ca.vijaysharma.resume.parcelable.ProjectSection;
import ca.vijaysharma.resume.parcelable.ProjectSectionItem;
import ca.vijaysharma.resume.parcelable.ReferenceSection;
import ca.vijaysharma.resume.parcelable.ReferenceSectionItem;
import ca.vijaysharma.resume.parcelable.Section;
import ca.vijaysharma.resume.parcelable.TextSection;
import ca.vijaysharma.resume.utils.Drawables;
import ca.vijaysharma.resume.utils.Intents;
import ca.vijaysharma.resume.utils.Metrics;
import ca.vijaysharma.resume.utils.ObservableScrollView;
import ca.vijaysharma.resume.utils.RoundedTransformation;

import static com.facebook.rebound.SpringUtil.mapValueFromRangeToRange;

/*
 TODO: Toolbar title should animate in/out on visibility
 TODO: Background for the status bar is currently a different view. It should be the background
 TODO: Action button hit areas should shrink with scroll
 TODO: Support swipe left/right with ViewPager
 */
public class DetailsActivity extends AppCompatActivity {
    private static final String PARCELABLE_DATA_KEY = "details";
    public static final RoundedTransformation ROUNDED_TRANSFORMATION = new RoundedTransformation();

    public static Intent start(Context context, DetailParcel...parcels) {
        Intent intent = new Intent(context, DetailsActivity.class);

        ArrayList<DetailParcel> data = new ArrayList<>();
        Collections.addAll(data, parcels);
        intent.putParcelableArrayListExtra(PARCELABLE_DATA_KEY, data);

        return intent;
    }

    private final Handler handler = new Handler();
    private final SpringSystem springSystem = SpringSystem.create();
    private Spring heroSpring;
    private Spring action1Spring;
    private Spring action2Spring;
    private Spring detail1Spring;
    private Spring detail2Spring;
    private Spring detail3Spring;
    private SimpleSpringListener heroSpringListener;
    private SimpleSpringListener action1SpringListener;
    private SimpleSpringListener action2SpringListener;
    private SimpleSpringListener detail1SpringListener;
    private SimpleSpringListener detail2SpringListener;
    private SimpleSpringListener detail3SpringListener;

    private DetailParcel detail;

    @Bind(R.id.scrollView) ObservableScrollView scrollView;
    @Bind(R.id.container) ViewGroup container;
    @Bind(R.id.background) View background;
    @Bind(R.id.status_bar_background) View statusBarBackground;
    @Bind(R.id.hero_image) ImageView hero;
    @Bind(R.id.description_container) LinearLayout descriptionContainer;
    @Bind(R.id.body) LinearLayout body;
    @Bind(R.id.description_1) TextView title1;
    @Bind(R.id.description_2) TextView title2;
    @Bind(R.id.description_3) TextView title3;
    @Bind(R.id.action_1) ImageButton action1;
    @Bind(R.id.action_2) ImageButton action2;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title) TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        final Point windowSize = Metrics.size(this);

        heroSpring = springSystem.createSpring();
        action1Spring = springSystem.createSpring();
        action2Spring = springSystem.createSpring();
        detail1Spring = springSystem.createSpring();
        detail2Spring = springSystem.createSpring();
        detail3Spring = springSystem.createSpring();
        heroSpring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 3));
        action1Spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 3));
        action2Spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 3));
        detail1Spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 16));
        detail2Spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 16));
        detail3Spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 16));
        heroSpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0);
                hero.setScaleX(mappedValue);
                hero.setScaleY(mappedValue);
            }
        };
        action1SpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                // TODO: the hit area still exists
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0);
                action1.setScaleX(mappedValue);
                action1.setScaleY(mappedValue);
            }
        };
        action2SpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                // TODO: the hit area still exists
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0);
                action2.setScaleX(mappedValue);
                action2.setScaleY(mappedValue);
            }
        };
        detail1SpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, windowSize.x, 0);
                title1.setTranslationX(mappedValue);
            }
        };
        detail2SpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, windowSize.x, 0);
                title2.setTranslationX(mappedValue);
            }
        };
        detail3SpringListener = new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float mappedValue = (float) mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, windowSize.x, 0);
                title3.setTranslationX(mappedValue);
            }
        };

        ArrayList<DetailParcel> details = getIntent().getParcelableArrayListExtra(PARCELABLE_DATA_KEY);
        detail = details.get(0);

        int toolbarHeight = Metrics.toolbarHeight(this);
        int marginFromEdge = (int)getResources().getDimension(R.dimen.margin_from_edge);
        int statusBarHeight = Metrics.statusBarHeight(this);
        applyInsets(container, statusBarHeight);

        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        toolbar.setNavigationIcon(detail.back());
        title.setText(detail.detail1());
        title.setTextColor(detail.secondaryColor());

        setActionBar(toolbar);
        getActionBar().setTitle(null);

        scrollView.addCallbacks(new ObservableScrollView.Callbacks() {
            @Override
            public void onScrollChanged(ObservableScrollView view, int deltaX, int deltaY) {
                handleScroll(view, detail);
            }
        });

        background.setBackgroundColor(detail.primaryColor());
        statusBarBackground.setBackgroundColor(detail.primaryColor());

        int heroDiameter = (int)getResources().getDimension(R.dimen.circle_item_diameter);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(heroDiameter, heroDiameter);
        frameLayoutParams.setMargins(marginFromEdge, toolbarHeight, 0, 0);
        hero.setLayoutParams(frameLayoutParams);
        hero.setBackground(Drawables.doubleBorderDrawable(
            this,
            detail.primaryColor(),
            detail.background()
        ));
        hero.setClickable(false);
        hero.setFocusable(false);

        frameLayoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );
        frameLayoutParams.setMargins(0, toolbarHeight, marginFromEdge, 0);
        frameLayoutParams.gravity = Gravity.END;
        descriptionContainer.setLayoutParams(frameLayoutParams);

        title1.setText(detail.detail1());
        title1.setVisibility(TextUtils.isEmpty(detail.detail1()) ? View.GONE : View.VISIBLE);
        title1.setTextColor(detail.secondaryColor());

        title2.setText(TextUtils.isEmpty(detail.detail2()) ? "" : detail.detail2());
        title3.setVisibility(TextUtils.isEmpty(detail.detail2()) ? View.GONE : View.VISIBLE);
        title2.setTextColor(detail.secondaryColor());

        title3.setText(TextUtils.isEmpty(detail.detail3()) ? "" : detail.detail3());
        title3.setVisibility(TextUtils.isEmpty(detail.detail3()) ? View.GONE : View.VISIBLE);
        title3.setTextColor(detail.tertiaryColor());

        int backgroundHeight = (int)getResources().getDimension(R.dimen.background_view_height);
        int actionItemDiameter = (int)getResources().getDimension(R.dimen.action_item_diameter);
        int actionItemRadius = actionItemDiameter / 2;
        int actionButtonTopMargin = backgroundHeight - actionItemRadius;

        int spaceBetweenActionItems = (int)getResources().getDimension(R.dimen.space_between_action_items);
        int action2Position = marginFromEdge + actionItemDiameter + spaceBetweenActionItems;
        frameLayoutParams = new FrameLayout.LayoutParams(actionItemDiameter, actionItemDiameter);
        frameLayoutParams.setMargins(0, actionButtonTopMargin, action2Position, 0);
        frameLayoutParams.gravity = Gravity.END;

        action1.setBackground(Drawables.rippleDrawable(this, detail.primaryColor()));
        action1.setLayoutParams(frameLayoutParams);

        frameLayoutParams = new FrameLayout.LayoutParams(actionItemDiameter, actionItemDiameter);
        frameLayoutParams.setMargins(0, actionButtonTopMargin, marginFromEdge, 0);
        frameLayoutParams.gravity = Gravity.END;

        action2.setBackground(Drawables.rippleDrawable(this, detail.primaryColor()));
        action2.setLayoutParams(frameLayoutParams);

        int bodyMarginTop = toolbarHeight + heroDiameter; // + statusBarHeight
        frameLayoutParams = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );
        frameLayoutParams.setMargins(marginFromEdge, bodyMarginTop, marginFromEdge, 0);
        body.setLayoutParams(frameLayoutParams);

        final ArrayList<Section> sections = detail.sections();
        for (int index = 0; index < sections.size(); index++) {
            Section section = sections.get(index);
            if (section instanceof TextSection) {
                TextSection textSection = (TextSection) section;
                addTextSection(detail.primaryColor(), textSection, body);
            } else if (section instanceof ReferenceSection) {
                ReferenceSection referenceSection = (ReferenceSection)section;
                addReferenceSection(detail.primaryColor(), referenceSection, body);
            } else if (section instanceof ProjectSection) {
                ProjectSection projectSection = (ProjectSection)section;
                addProjectSection(detail.primaryColor(), projectSection, body);
            }
        }

//        DesignSpec designSpec = DesignSpec.fromResource(scrollView, R.raw.details_spec);
//        designSpec.setKeylinesColor(getResources().getColor(R.color.yellow));
//        designSpec.setSpacingsColor(getResources().getColor(R.color.yellow));
//        designSpec.setBaselineGridColor(getResources().getColor(R.color.yellow));
//        scrollView.getOverlay().add(designSpec);

    }

    @Override
    protected void onStart() {
        super.onStart();

        heroSpring.addListener(heroSpringListener);
        action1Spring.addListener(action1SpringListener);
        action2Spring.addListener(action2SpringListener);
        detail1Spring.addListener(detail1SpringListener);
        detail2Spring.addListener(detail2SpringListener);
        detail3Spring.addListener(detail3SpringListener);

        final Point windowSize = Metrics.size(this);

        int heroImageDiameter = (int)getResources().getDimension(R.dimen.circle_item_diameter);
        heroSpring.setCurrentValue(0);
        Picasso.with(this)
            .load(detail.hero())
            .placeholder(R.color.background_color)
            .centerCrop()
            .transform(ROUNDED_TRANSFORMATION)
            .resize(heroImageDiameter, heroImageDiameter)
            .into(hero);

        detail1Spring.setCurrentValue(0);
        title1.setTranslationX(windowSize.x);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail1Spring.setEndValue(1);
            }
        }, 600);

        detail2Spring.setCurrentValue(0);
        title2.setTranslationX(windowSize.x);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail2Spring.setEndValue(1);
            }
        }, 700);

        detail3Spring.setCurrentValue(0);
        title3.setTranslationX(windowSize.x);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail3Spring.setEndValue(1);
            }
        }, 400);

        action1Spring.setCurrentValue(1);
        action1.setScaleX(0);
        action1.setScaleY(0);
        Picasso.with(this)
            .load(detail.action1().action())
            .placeholder(R.color.background_color)
            .into(action1, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            action1Spring.setEndValue(0);
                        }
                    }, 200);
                    action1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(detail.action1().intent());
                        }
                    });
                }
            });
        action1.setVisibility(Intents.isEmpty(detail.action1().intent()) ? View.GONE : View.VISIBLE);

        action2Spring.setCurrentValue(1);
        action2.setScaleX(0);
        action2.setScaleY(0);
        Picasso.with(this)
            .load(detail.action2().action())
            .placeholder(R.color.background_color)
            .into(action2, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            action2Spring.setEndValue(0);
                        }
                    }, 300);
                    action2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(detail.action2().intent());
                        }
                    });
                }
            });
        action2.setVisibility(Intents.isEmpty(detail.action2().intent()) ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        heroSpring.removeListener(heroSpringListener);
        action1Spring.removeListener(action1SpringListener);
        action2Spring.removeListener(action2SpringListener);
        detail1Spring.removeListener(detail1SpringListener);
        detail2Spring.removeListener(detail2SpringListener);
        detail3Spring.removeListener(detail3SpringListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sectionTitle(
        int primaryColor,
        String titleString,
        LinearLayout linearLayout
    ) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        final TextView title = (TextView) inflater.inflate(R.layout.text_detail_section_header, linearLayout, false);
        title.setText(titleString);
        title.setTextColor(primaryColor);
        linearLayout.addView(title);
    }

    private void addReferenceSection(
        int primaryColor,
        ReferenceSection detail,
        LinearLayout linearLayout
    ) {
        final ArrayList<ReferenceSectionItem> items = detail.items();
        if (items.isEmpty())
            return;

        sectionTitle(primaryColor, detail.name(), linearLayout);

        int itemSpacer = (int) getResources().getDimension(R.dimen.space_between_body_items);
        int firstItemSpacer = (int)getResources().getDimension(R.dimen.space_between_body_first_item);

        final LayoutInflater inflater = LayoutInflater.from(this);

        for (int index = 0; index < items.size(); index++) {
            ReferenceSectionItem section = items.get(index);
            View view = inflater.inflate(R.layout.reference_detail_section_body, linearLayout, false);

            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(section.name());
            name.setTranslationY(-10.0f);
            name.setAlpha(0);
            name.animate()
                .setStartDelay(400)
                .setDuration(500)
                .translationY(0)
                .alpha(1)
                .start();

            TextView position = (TextView) view.findViewById(R.id.position);
            position.setText(section.position());
            position.setTranslationY(-15.0f);
            position.setAlpha(0);
            position.animate()
                .setStartDelay(400)
                .setDuration(500)
                .translationY(0)
                .alpha(1)
                .start();

            final ImageView avatar = (ImageView) view.findViewById(R.id.avatar);
            avatar.setScaleX(0);
            avatar.setScaleY(0);
            Picasso.with(this)
                .load(section.avatarUrl())
                .placeholder(R.color.background_color)
                .transform(ROUNDED_TRANSFORMATION)
                .into(avatar, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        avatar.animate()
                            .setStartDelay(100)
                            .setDuration(700)
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setInterpolator(new OvershootInterpolator(1.5f))
                            .start();
                    }
                });

            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            linearLayoutParams.setMargins(0, index == 0 ? firstItemSpacer : itemSpacer, 0, 0);
            LinearLayout container = (LinearLayout) view.findViewById(R.id.reference_container);
            container.setLayoutParams(linearLayoutParams);

            linearLayout.addView(container);
        }
    }

    private void addProjectSection(final int primaryColor, ProjectSection section, LinearLayout linearLayout) {
        ArrayList<ProjectSectionItem> items = section.items();
        if (items.isEmpty())
            return;

        sectionTitle(primaryColor, section.name(), linearLayout);

        int itemSpacer = (int)getResources().getDimension(R.dimen.space_between_project_items);
        int firstItemSpacer = (int)getResources().getDimension(R.dimen.space_between_body_first_item);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int index = 0; index < items.size(); index++) {
            final ProjectSectionItem project = items.get(index);
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            linearLayoutParams.setMargins(0, index == 0 ? firstItemSpacer : itemSpacer, 0, 0);
            SpannableStringBuilder output = createProjectName(project, primaryColor);
            View view = inflater.inflate(R.layout.project_detail_section_body, linearLayout, false);
            view.setLayoutParams(linearLayoutParams);

            TextView content = (TextView)view.findViewById(R.id.header);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setText(output);
            content.setTranslationY(-15.0f);
            content.setAlpha(0);
            content.animate()
                .setStartDelay(400)
                .setDuration(500)
                .translationY(0)
                .alpha(1)
                .start();

            RecyclerView list = (RecyclerView)view.findViewById(R.id.image_container);
            list.setHasFixedSize(true);
            list.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
            list.setAdapter(new LayoutAdapter(this, project.locals(), project.remotes()));
            list.setFocusable(false);
            list.setVisibility(project.locals().size() + project.remotes().size() == 0 ? View.GONE : View.VISIBLE);
            linearLayout.addView(view);
        }
    }

    private SpannableStringBuilder createProjectName(final ProjectSectionItem project, @ColorRes final int primaryColor) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(project.name(), new StyleSpan(Typeface.BOLD), 0);
        if (! Uri.EMPTY.equals(project.link())) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint tp) {
                    tp.setColor(primaryColor);
                }

                @Override
                public void onClick(View widget) {
                    startActivity(Intents.createUrlIntent(project.link()));
                }
            };
            builder.append(" - ")
                .append("Link", clickableSpan, 0);
        }

        if (! TextUtils.isEmpty(project.description())) {
            builder.append("\n").append(project.description());
        }

        return builder;
    }

    private void addTextSection(
        @ColorRes int primaryColor,
        TextSection detail,
        LinearLayout linearLayout
    ) {
        final ArrayList<String> items = detail.items();
        if (items.isEmpty())
            return;

        sectionTitle(primaryColor, detail.name(), linearLayout);

        int leftMargin = (int)getResources().getDimension(R.dimen.body_section_margin);
        int rightMargin = (int)getResources().getDimension(R.dimen.margin_from_edge);
        int itemSpacer = (int)getResources().getDimension(R.dimen.space_between_body_items);
        int firstItemSpacer = (int)getResources().getDimension(R.dimen.space_between_body_first_item);

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int index = 0; index < items.size(); index++) {
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            linearLayoutParams.setMargins(leftMargin, index == 0 ? firstItemSpacer : itemSpacer, rightMargin, 0);
            String item = items.get(index);
            TextView content = (TextView) inflater.inflate(R.layout.text_detail_section_body, linearLayout, false);
            content.setLayoutParams(linearLayoutParams);
            content.setText(item);
            content.setTranslationY(-15.0f);
            content.setAlpha(0);
            content.animate()
                .setStartDelay(400)
                .setDuration(500)
                .translationY(0)
                .alpha(1)
                .start();

            linearLayout.addView(content);
        }
    }

    private static void applyInsets(ViewGroup container, final int toolbarHeight) {
        container.setFitsSystemWindows(true);
        container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                view.setPadding(0, toolbarHeight, 0, insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            }
        });
    }

    private void handleScroll(ObservableScrollView view, DetailParcel detail) {
        int maxHeight = Metrics.toolbarHeight(this);
        int backgroundHeight = (int)getResources().getDimension(R.dimen.background_view_height);

        heroSpring.setEndValue(view.getScrollY() > maxHeight ? 1 : 0);
        action1Spring.setEndValue(view.getScrollY() > maxHeight - 15 ? 1 : 0);
        action2Spring.setEndValue(view.getScrollY() > maxHeight - 30 ? 1 : 0);

        int difference = backgroundHeight - maxHeight;
        title.setVisibility(view.getScrollY() > difference ? View.VISIBLE : View.INVISIBLE);
        int primaryColor = detail.primaryColor();
        toolbar.setBackgroundColor(
            view.getScrollY() > difference ? primaryColor : ContextCompat.getColor(this, android.R.color.transparent)
        );

        final int endValue = view.getScrollY() > maxHeight ? 0 : 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail1Spring.setEndValue(endValue);
            }
        }, 200);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail2Spring.setEndValue(endValue);
            }
        }, 300);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                detail3Spring.setEndValue(endValue);
            }
        }, 400);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        public ViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }

    private static class LayoutAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final Context context;
        private final List<Integer> locals;
        private final List<Uri> remotes;

        public LayoutAdapter(Context context, List<Integer> locals, List<Uri> remotes) {
            this.context = context;
            this.locals = locals;
            this.remotes = remotes;
        }

        @Override
        public int getItemCount() {
            return locals.size() + remotes.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
            ));
            return new ViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int majorSpacing = (int)context.getResources().getDimension(R.dimen.body_section_margin);
            int minorSpacing = (int)context.getResources().getDimension(R.dimen.image_item_spacing);
            holder.imageView.setPadding(position == 0 ? majorSpacing : minorSpacing, 0, 0, 0);

            if (position < locals.size()) {
                Picasso.with(context).load(locals.get(position)).into(holder.imageView);
            } else {
                int remotePosition = position - locals.size();
                Picasso.with(context).load(remotes.get(remotePosition)).into(holder.imageView);
            }
        }
    }
}

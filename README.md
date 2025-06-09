# Model_UN
A Java-based UNA-USA style Model UN Chairing program with a GUI built on JavaFX. **I will update the README later. For more information, just search on the web or ask AI**

## What is MUN?

Model United Nations (MUN) is an educational simulation of the United Nations where students take on the roles of diplomats representing different countries or organizations. Participants debate global issues, draft resolutions, and negotiate with others to address real-world challenges—all while following formal parliamentary procedure. The UNA-USA style, one of the mainstream Model UN conference styles used widely across the US, focuses on flexibility and is noted for being beginner friendly while maintaining a sense of competition among delegates.

#### Key Components of an MUN Conference:
- Delegates – Participants who represent a country or entity and advocate for its policies.
  - country name (String)
  - Since the conference is UNA-USA style, delegates are kept track of their performance, which are affected by the following properties of delegates:
    - Number of POIs raised (Point Of Information - a question asked by a delegate to another delegate after the latter finishes their speech)
    - Number of Amendments raised
    - Number of Speeches given
    - Whether the delegate is a main submitter
    - Whether the delegate is present and voting during roll call ('present and voting' means the delegate **has** to take a stance in voting for an amendment or a resolution. It gives a sense of confidence of the country's stance toward the topic. 'present' does not give that restriction.)

- Committee – A group of delegates discussing a specific topic (e.g., Security Council, WHO).
  - Amendments, resolutions, and voting procedures are discussed on the Committee.

- Chair (Moderator) – Oversees debate, enforces procedure, and ensures smooth discussion.

- Phases of Debate – Typically includes:

- Roll Call – Attendance and country representation.

- Speakers’ List – Delegates deliver formal speeches.

- Moderated & Unmoderated Caucuses – Informal discussion and negotiation.

- Resolution Drafting – Writing proposed solutions.

- Voting Procedure – Passing or rejecting resolutions.

- Resolutions & Amendments – Formal documents outlining solutions, which can be modified through amendments.

#### Motivation
Motivated initially for fun and later for potential for automation of various often cumbersome tasks (like keeping track of which phase the conference is in, formatting resolutions, and handling amendments), I created this app. My current code is incomplete, and it is missing very critical components like resolution and amendment handling, as well as potential restructuring of the code for better and more convenient GUI management and maintainability. Nevertheless, here is what I expect this app to do:

### 1. Initialization
The app will ask users whether to load a past conference from file (json) or to create a new one. For a new conference creation, the user would be able to 
